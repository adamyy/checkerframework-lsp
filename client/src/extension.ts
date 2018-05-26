import * as vscode from "vscode"
import * as path from 'path';
import * as fs from 'fs';
import { LanguageClient, LanguageClientOptions, ServerOptions, TransportKind } from 'vscode-languageclient';
import { workspace, ExtensionContext } from "vscode";

export function activate(context: ExtensionContext) {
	let serverModule = context.asAbsolutePath(path.join('server', 'server.js'));
	let debugOptions = { execArgv: ["--nolazy", "--debug=6009"] };

	let serverOptions: ServerOptions = {
		run : { module: serverModule, transport: TransportKind.ipc },
		debug: { module: serverModule, transport: TransportKind.ipc, options: debugOptions }
	}

	let clientOptions: LanguageClientOptions = {
		documentSelector: ['java'],
		synchronize: {
			configurationSection: 'checker-framework',
			fileEvents: workspace.createFileSystemWatcher('**/.clientrc')
		}
	}
	let disposable = new LanguageClient('checker-framework', 'Checker Framework', serverOptions, clientOptions).start();
	context.subscriptions.push(disposable);
}
