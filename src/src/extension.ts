import * as vscode from "vscode"

import { diagnosticCollection, setDiagnosticCollection, updateConfigurations } from "./diagnostics"

export async function activate(context: vscode.ExtensionContext) {
	context.subscriptions.push(
		diagnosticCollection,
		vscode.workspace.onDidOpenTextDocument(setDiagnosticCollection),
		vscode.workspace.onDidSaveTextDocument(setDiagnosticCollection),
		vscode.workspace.onDidChangeConfiguration(updateConfigurations),
	)
}

export function deactivate() { }