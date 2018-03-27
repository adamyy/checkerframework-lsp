'use strict';

import {
	IPCMessageReader, IPCMessageWriter,
	createConnection, IConnection, TextDocumentSyncKind,
	TextDocuments, TextDocument, Diagnostic, DiagnosticSeverity,
	InitializeParams, InitializeResult
} from 'vscode-languageserver';

import * as fs from 'fs';
import * as path from 'path';
import { connect } from 'net';
import { platform } from "os";
import { uriToFilePath } from 'vscode-languageserver/lib/files';
import { WorkspaceFoldersFeature } from 'vscode-languageserver/lib/workspaceFolders';

const { spawn } = require('child_process');

// Create a connection for the server. The connection uses Node's IPC as a transport
let connection: IConnection = createConnection(new IPCMessageReader(process), new IPCMessageWriter(process));
connection.listen();
let documents: TextDocuments = new TextDocuments();
documents.listen(connection);

let workspaceRoot: string;
let maxNumberOfProblems: number;
let enable: boolean;
let checkers: any;
let classpath: any;
let child: any;

let compilerpath = `:${__dirname}/../compiler`;
let checkerpath = `:${process.env.CHECKERFRAMEWORK}/checker/dist/checker.jar`;
let jdkpath = `${process.env.CHECKERFRAMEWORK}/checker/dist/jdk8.jar`;
let gsonpath = `:${process.env.GSON}`;

// The settings interface describe the server relevant settings part
interface Settings {
	checkerFramework: CheckerSettings;
}
interface CheckerSettings {
	maxNumberOfProblems: number;
	enable: boolean;
	checkers: any;
	classpath: any;
}

function uriToPath(uri: string): string {
	let filepath = decodeURI(uri.replace("file://",""));
	if (platform() == 'win32') {
		filepath = filepath.substr(1).replace(/%3A/g, ':').replace(/\//g, '\\');
	}
	return filepath;
}

connection.onInitialize((params): InitializeResult => {	
	workspaceRoot = params.rootPath;
	return {		
		capabilities: {
			// Tell the client that the server works in FULL text document sync mode
			textDocumentSync: documents.syncKind
		}
	}
});

// The settings have changed.
connection.onDidChangeConfiguration((change) => {
	let settings = <Settings>change.settings;
	maxNumberOfProblems = settings["checker-framework"].maxNumberOfProblems || 100;
	enable = settings["checker-framework"].enable || false;
	checkers = settings["checker-framework"].checkers.join(":") || "";
	classpath = `:${settings["checker-framework"].classpath.join(":")}` || "";
	if (classpath == ":") {
		classpath = "";
	}
	if (child == null) {
		child = spawn('java',['-cp', `.${compilerpath}${gsonpath}${checkerpath}${classpath}`, 'RunCompiler']);	
		child.on('exit', function (code, signal) {
			console.log('child process exited with ' +
						`code ${code} and signal ${signal}`);
		});
	
		child.stdout.on('data', (data)=>{
			sendDiagnostics(data);
		});	
	}
	for (let doc of documents.all()) {
		validateJavaDocument(doc.languageId, doc.uri);
	}
});

connection.onDidOpenTextDocument((change) => {
	validateJavaDocument(change.textDocument.languageId,change.textDocument.uri);
});

connection.onDidSaveTextDocument((change) => {
	let s = uriToFilePath(change.textDocument.uri)
	validateJavaDocument(s.substr(s.length-4), change.textDocument.uri);
});

let request = require('request');

function sendDiagnostics(data: string): void {
	let diagnostics: Diagnostic[] = [];
	let numberOfProblems = JSON.parse(data).count;
	let uri = JSON.parse(data).uri;		
	let diagnosticsList = JSON.parse(data).list;
	for(let i=0; i<Math.min(numberOfProblems, maxNumberOfProblems); i++){
		let diganosticItem = diagnosticsList[i];
		if (diganosticItem.line <= 0 || diganosticItem.start <= 0 || diganosticItem.end <= 0){
			console.log(diganosticItem.message);
		} else {
			diagnostics.push({
				severity: diganosticItem.severity,
				range: {
					start: { line: diganosticItem.line-1, character: diganosticItem.start-1 },
					end: { line: diganosticItem.line-1, character: diganosticItem.end-1 }
				},
				message: diganosticItem.message,
				source: 'javac'
			});
		}	
	}
	if (numberOfProblems <= 0) {
		diagnostics = [];
	}
	connection.sendDiagnostics({ uri: uri, diagnostics });
}

function validateJavaDocument(languageId: string, uri: string): void {
	if(!enable || languageId != "java") return;
	let diagnostics: Diagnostic[] = [];
	let filePath = uriToPath(uri);
	let requestBody = `${checkers},${filePath}`;
	console.log(`validateJava checkers: ${checkers}`);
	child.stdin.write(`${uri},${checkers},${filePath},${jdkpath}\n`);
	console.log("child process still alive");	
}