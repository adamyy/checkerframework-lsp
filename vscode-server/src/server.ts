'use strict';

import {
	IPCMessageReader, IPCMessageWriter,
	createConnection, IConnection, TextDocumentSyncKind,
	TextDocuments, TextDocument, Diagnostic, DiagnosticSeverity,
	InitializeParams, InitializeResult, ShowMessageRequest, MessageType,
	DidChangeConfigurationParams, DidSaveTextDocumentParams, DidOpenTextDocumentParams, DidChangeTextDocumentParams
} from 'vscode-languageserver';
import { WorkspaceFoldersFeature } from 'vscode-languageserver/lib/workspaceFolders';

import { checkerSettings, checkerPath, frameworkPath, javacLibPath, jdkPath, onConfigurationChanges } from './settings';
import { uriToPath, pathToUri } from './utils';
import { DiagnosticsItem, DiagnosticsResponse, toVscodeSeverity } from './protocol'

const { spawn } = require('child_process');

// Create a connection for the server. The connection uses Node's IPC as a transport
let connection: IConnection = createConnection(new IPCMessageReader(process), new IPCMessageWriter(process));
connection.listen();
let documents: TextDocuments = new TextDocuments();
documents.listen(connection);

let workspaceRoot: string;
let javacProcess: any;

connection.onInitialize((params): InitializeResult => {
	workspaceRoot = params.rootPath;
	return {
		capabilities: {
			// Tell the client that the server works in FULL text document sync mode
			textDocumentSync: documents.syncKind
		}
	}
});

connection.onDidChangeConfiguration((change: DidChangeConfigurationParams) => {
	console.log(`Changed configuration ${change.settings}`);
	onConfigurationChanges(change);

	if (frameworkPath() == "") {
		let msg = {
			type: MessageType.Error,
			message: "Missing path to checker framework, please specify in user settings"
		};
		let req = connection.sendRequest('window/showMessageRequest', msg);
		return;
	}

	if (javacProcess == null) {
		console.log(`Starting javac process...`);
		let classpath = `.:${checkerPath()}:${javacLibPath()}/*`;
		let mainClass = 'org.checkerframework.vscode.RunJavac';
		javacProcess = spawn('java', ['-cp', classpath, mainClass]);

		javacProcess.on('exit', function (code, signal) {
			console.log(`child process exited with code ${code} and signal ${signal}`);
		});

		javacProcess.stdout.on('data', (data) => {
			sendDiagnostics(data);
		});
	}

	documents.all().forEach((doc : TextDocument) => {
		validateJavaDocument(doc.languageId, doc.uri);
	});
});

connection.onDidOpenTextDocument((change: DidOpenTextDocumentParams) => {
	console.log(`Opened file ${change.textDocument.uri}`);
	validateJavaDocument(change.textDocument.languageId, change.textDocument.uri);
});

connection.onDidSaveTextDocument((change: DidSaveTextDocumentParams) => {
	console.log(`Saved file ${change.textDocument.uri}`);
	let fileExtension = change.textDocument.uri.split('.').pop();
	validateJavaDocument(fileExtension, change.textDocument.uri);
});

function sendDiagnostics(data: string) {
	let response = <DiagnosticsResponse>JSON.parse(data);
	console.log(`Parsed diagnostic: ${JSON.stringify(response)}}`);

	let invalidDianostic = (diagnosticItem: DiagnosticsItem) => {
		return diagnosticItem.line <= 0 || diagnosticItem.start <= 0 && diagnosticItem.end <= 0
	};

	response.diagnosticsItemList
		.filter(invalidDianostic)
		.forEach(diagnosticItem => {
			let msg = { type: MessageType.Error, message: diagnosticItem.message };
			let req = connection.sendRequest('window/showMessageRequest', msg);
			console.log(`Invalid diagnostic item found: ${diagnosticItem.message}`);
		});

	let vscodeDiagnostics: Diagnostic[] = response.diagnosticsItemList
		.filter(diagnosticItem => !invalidDianostic(diagnosticItem) )
		.map(diagnosticItem => {
			return {
				severity: toVscodeSeverity(diagnosticItem.severity),
				range: {
					start: { line: diagnosticItem.line - 1, character: diagnosticItem.start - 1 },
					end: { line: diagnosticItem.line - 1, character: diagnosticItem.end - 1 }
				},
				message: diagnosticItem.message,
				source: 'javac'
			}
		})
		.slice(0, checkerSettings.maxNumberOfProblems);

	connection.sendDiagnostics({ uri: response.uri, diagnostics: vscodeDiagnostics });
}

function validateJavaDocument(languageId: string, uri: string) {
	if (!checkerSettings.enable || languageId != "java") return;
	console.log(`Sending diagnostic request for ${uri}`);
	let filePath = uriToPath(uri);
	let checkers = checkerSettings.checkers.join(":");
	let requestBody = `${checkers},${filePath}`;
	javacProcess.stdin.write(`${uri},${checkers},${filePath},${jdkPath()}\n`);
}