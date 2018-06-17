import * as path from 'path';
import * as fs from 'fs';
import { LanguageClient, LanguageClientOptions, ServerOptions, ExecutableOptions } from 'vscode-languageclient';
import { workspace, ExtensionContext } from "vscode";

export function activate(context: ExtensionContext) {
	console.log(getExecutableCommand());
	console.log(getExecutableArgs());
	console.log(getExecutableOptions());

	let serverOptions: ServerOptions = {
		command: getExecutableCommand(),
		args: getExecutableArgs(),
		options: getExecutableOptions(),
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

export function deactivate() {
	// no-op
}

function getExecutableCommand(): string {
	let binName = /^win/.test(process.platform) ? 'java.exe' : 'java';
	let javaHomeCandidates = [workspace.getConfiguration('java').get<string>('home', null), process.env['JDK_HOME'], process.env['JAVA_HOME']];
	
	let result = javaHomeCandidates.find((candidate) => {
		if (!candidate) return false;

		return fs.existsSync(path.join(candidate, 'bin', binName));
	});

	if (result) return path.join(result, 'bin', binName);

	result = process.env['PATH'].split(path.delimiter).find((candidate) => {
		if (!candidate) return false;

		return fs.existsSync(path.join(candidate, binName));
	})

	return path.join(result, binName);
}

function getExecutableArgs(): string[] {
	let frameworkPath = workspace.getConfiguration('checker-framework').get<string>('frameworkPath');
	let checkerPath = `${frameworkPath}/checker/dist/checker.jar`;
	let fatJarPath = `${__dirname}/libs/lsp-javac-0.1.0.jar`;
	let classpath = `.:${checkerPath}:${fatJarPath}`;
	let mainClass = 'org.checkerframework.LanguageServerStarter';
	return ['-cp', classpath, mainClass];
}

function getExecutableOptions(): ExecutableOptions {
	return {
		cwd: workspace.workspaceFolders[0].uri.toString(),
	};
}
