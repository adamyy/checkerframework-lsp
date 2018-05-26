'use strict';

import { DiagnosticSeverity } from 'vscode-languageserver-types'

export interface DiagnosticsItem {
	message: string,
	severity: string,
	line: number,
	start: number,
	end: number
}

export interface DiagnosticsResponse {
	uri: string,
	compiledWithoutError: boolean,
	diagnosticsItemList: DiagnosticsItem[],
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