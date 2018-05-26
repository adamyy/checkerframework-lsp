'use strict';

import {
	IPCMessageReader, IPCMessageWriter,
	createConnection, IConnection, TextDocumentSyncKind,
	TextDocuments, TextDocument, Diagnostic, DiagnosticSeverity,
	InitializeParams, InitializeResult, ShowMessageRequest, MessageType,
	DidChangeConfigurationParams, DidSaveTextDocumentParams, DidOpenTextDocumentParams, DidChangeTextDocumentParams
} from 'vscode-languageserver';
import { WorkspaceFoldersFeature } from 'vscode-languageserver/lib/workspaceFolders';
import URI from 'vscode-uri';

import * as fs from 'fs';
import * as path from 'path';
import { connect } from 'net';
import { platform } from "os";

import * as settings from './settings';
import { uriToPath, pathToUri } from './protocol-translation';
import { execFile } from 'child_process';

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
	settings.onConfigurationChanges(change);

	if (settings.frameworkPath() == "") {
		let msg = {
			type: MessageType.Error,
			message: "Missing path to checker framework, please specify in user settings"
		};
		let req = connection.sendRequest('window/showMessageRequest', msg);
		return;
	}

	if (javacProcess == null) {
		let classpath = `.:${settings.compilerPath}:${settings.checkerPath()}:${settings.libPath}/*`;
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
	let fileUri = URI.parse(change.textDocument.uri);
	let fileExtension = fileUri.fsPath.split('.').pop();
	validateJavaDocument(fileExtension, fileUri.fsPath);
});

function sendDiagnostics(data: string): void {
	let diagnostic = JSON.parse(data);

	let uri = diagnostic.uri;
	let diagnosticsList = diagnostic.list;

	let invalidDianostic = diagnosticItem => {
		return diagnosticItem.line <= 0 || diagnosticItem.start <= 0 && diagnosticItem.end <= 0
	};

	diagnosticsList
		.filter(invalidDianostic)
		.forEach(diagnosticItem => {
			let msg = { type: MessageType.Error, message: diagnosticItem.message };
			let req = connection.sendRequest('window/showMessageRequest', msg);
			console.log(`Invalid diagnostic item found: ${diagnosticItem.message}`);
		});

	let diagnostics: Diagnostic[] = diagnosticsList
		.filter(diagnosticItem => !invalidDianostic(diagnosticItem) )
		.map(diagnosticItem => {
			return {
				severity: diagnosticItem.severity,
				range: {
					start: { line: diagnosticItem.line - 1, character: diagnosticItem.start - 1 },
					end: { line: diagnosticItem.line - 1, character: diagnosticItem.end - 1 }
				},
				message: diagnosticItem.message,
				source: 'javac'
			}
		})
		.slice(0, settings.checkerSettings.maxNumberOfProblems);

	console.log(JSON.stringify({ uri: uri, diagnostics: diagnostics }));
	connection.sendDiagnostics({ uri: uri, diagnostics: diagnostics });
}

function validateJavaDocument(languageId: string, uri: string): void {
	if (!settings.checkerSettings.enable || languageId != "java") return;
	let filePath = uriToPath(uri);
	let checkers = settings.checkerSettings.checkers.join(":");
	let requestBody = `${checkers},${filePath}`;
	javacProcess.stdin.write(`${uri},${checkers},${filePath},${settings.jdkPath()}\n`);
}