import * as vscode from "vscode";
import { exec, spawn } from "child_process";
import { platform } from "os";
import { Diagnostic, Range, Position } from "vscode";

class Counter {
	static processes = 0;
	static limit() {
		return 3;
	}
}

export const diagnosticCollection = vscode.languages.createDiagnosticCollection("java");

let config = vscode.workspace.getConfiguration("checker-framework");
let maxNumberOfProblems = config.maxNumberOfProblems || 20;
let enable = config.enable;
let checkers = config.checkers.join(" ") || "";

const child_javac = spawn('wc');

// TODO: initialize JVM, need help
// const child_javac = spawn('java',[]);

function uriToPath(uri: string): string {
	let filepath = decodeURI(uri.replace("file://",""));
	if (platform() == 'win32') {
		filepath = filepath.substr(1).replace(/%3A/g, ':').replace(/\//g, '\\');
	}
	return filepath;
}

function pathToUri(path: string): vscode.Uri {
	return vscode.Uri.file(path);
}

child_javac.stdout.on('data', (data)=> {
	console.log(data.toString());
	// parseJavacOut(data.toString());
	Counter.processes -= 1;
});

export function parseJavacOut(stdout) {

	let diagnostics: Diagnostic[] = [];
	let filepath = "";

	let firstMsg = stdout.split(':')[1].trim();
	if (firstMsg == "directory not found" || firstMsg == "invalid flag") {
		console.error(firstMsg);
		diagnosticCollection.clear();
		return;
	}else {
		filepath = stdout.split(':')[0].trim();
	}

	let errors = stdout.split(filepath);
	let detailedMsg = "";
	var lines = [];
	var problemsCount = 0;
	
	errors.forEach((error) => {
		let firstLine = error.split('\n')[0].split(':');
		let lineNumber = parseInt(firstLine[1].trim(),10);
		let severity = firstLine[2].trim();
		let detailedMsg = firstLine[3].split('$$');
		let message = detailedMsg[0].trim();
		let columnEnclosure = detailedMsg[4].split(',');
		let start = parseInt(columnEnclosure[0].split('(')[1].trim(),10);
		let end = parseInt(columnEnclosure[1].split(')')[0].trim(),10);

		diagnostics.push(new vscode.Diagnostic(
			new Range(new Position(lineNumber,start),new Position(lineNumber,end)),
			message,
			severity));
	});

	diagnosticCollection.set(pathToUri(filepath), diagnostics);
}

export function setDiagnosticCollection(document: vscode.TextDocument) {
	if (Counter.processes < Counter.limit() && enable && document.languageId == "java") {
		Counter.processes += 1;
		try{
			let filepath = uriToPath(document.uri.toString());
			console.log(child_javac.stdin.write("Hello World"));
			child_javac.stdin.end();

			// TODO: When stdin end, will the JVM stop running?
			// child_javac.stdin.write(`-Adetailedmsgtext -processor ${checkers} ${filepath}`);
			// child_javac.stdin.end();
		} catch(e){
			diagnosticCollection.clear();
			Counter.processes -= 1;
			console.log(e);
		}
	}
}

export function updateConfigurations(change) {
	config = vscode.workspace.getConfiguration("checker-framework");
	maxNumberOfProblems = config.maxNumberOfProblems || 20;
	enable = config.enable;
	checkers = config.checkers.join(" ") || "";

	// TODO: resolve "write after end" error
	// vscode.workspace.textDocuments.forEach(setDiagnosticCollection);
}