'use strict';

import { DidChangeConfigurationParams } from 'vscode-languageserver';

interface Settings {
	readonly checkerFramework: CheckerSettings;
}

interface CheckerSettings {
	readonly maxNumberOfProblems: number;
	readonly enable: boolean;
	readonly checkers: string[];
    readonly frameworkpath: string;
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
    return checkerSettings.frameworkpath;
}

export function checkerPath(): string {
    return `${checkerSettings.frameworkpath}/checker/dist/checker.jar`;
}

export function jdkPath(): string {
    return `${checkerSettings.frameworkpath}/checker/dist/jdk8.jar`;
}

export function outputDir(workspaceRoot: string): string {
    return `${workspaceRoot}/${checkerSettings.tempOutputDir}`;
}