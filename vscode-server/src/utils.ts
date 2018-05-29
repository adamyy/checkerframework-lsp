'use strict';

import { DiagnosticSeverity } from 'vscode-languageserver-types'

export function isWindows(): boolean {
    return /^win/.test(process.platform);
}

export function uriToPath(uri: string): string {
	let filepath = decodeURI(uri.replace("file://", ""));
	if (isWindows()) {
		filepath = filepath.substr(1).replace(/%3A/g, ':').replace(/\//g, '\\');
	}
	return filepath;
}

export function pathToUri(p: string): string {
    return 'file://' + (isWindows() ? '/' + p.replace(/\//g, '/') : p);
}

export function toVscodeSeverity(severity: string): DiagnosticSeverity {
	const dict = {
		"ERROR": DiagnosticSeverity.Error,
		"WARNING": DiagnosticSeverity.Warning,
		"MANDATORY_WARNING": DiagnosticSeverity.Warning,
		"NOTE": DiagnosticSeverity.Information,
		"OTHER": DiagnosticSeverity.Information
	}
	return dict[severity];
}