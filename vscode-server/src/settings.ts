'use strict';

import { DidChangeConfigurationParams } from 'vscode-languageserver';

interface Settings {
	readonly checkerFramework: CheckerSettings;
}

interface CheckerSettings {
	readonly maxNumberOfProblems: number;
	readonly enable: boolean;
	readonly checkers: string[];
    readonly frameworkPath: string;
    readonly tempOutputDir: string;
}

export let checkerSettings: CheckerSettings;

export function onConfigurationChanges(change: DidChangeConfigurationParams) {
    checkerSettings = <CheckerSettings>change.settings['checker-framework'];
}

export function javacLibPath(): string {
    return `${__dirname}/lsp-javac/lib`;
}

export function frameworkPath(): string {
    return checkerSettings.frameworkPath;
}

export function checkerPath(): string {
    return `${checkerSettings.frameworkPath}/checker/dist/checker.jar`;
}

export function jdkPath(): string {
    return `${checkerSettings.frameworkPath}/checker/dist/jdk8.jar`;
}

export function outputDir(workspaceRoot: string): string {
    return `${workspaceRoot}/${checkerSettings.tempOutputDir}`;
}