import { DidChangeConfigurationParams } from 'vscode-languageserver';

interface Settings {
	readonly checkerFramework: CheckerSettings;
}

interface CheckerSettings {
	readonly maxNumberOfProblems: number;
	readonly enable: boolean;
	readonly checkers: string[];
    readonly frameworkpath: string;
}

let checkerSettings: CheckerSettings;
let compilerPath = `${__dirname}/../compiler`;
let libPath = `${__dirname}/vscode-extension/lib`;
export { checkerSettings, compilerPath, libPath };

export function onConfigurationChanges(change: DidChangeConfigurationParams) {
    checkerSettings = <CheckerSettings>change.settings['checker-framework'];
}

export function frameworkPath(): string {
    return checkerSettings.frameworkpath;
}

export function checkerPath(): string {
    return `${checkerSettings.frameworkpath}/checker/dist/checker.jar`;
}

export function jdkPath(): string {
    return `${checkerSettings.frameworkpath}/checker/dist/jdk8.jar`;
}