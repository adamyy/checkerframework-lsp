'use strict';

export interface DiagnosticsRequest {
	uri: string,
	checkers: string[],
	filePath: string,
	jarPath: string,
	outputDir: string
}

export interface DiagnosticsResponse {
	uri: string,
	diagnosticsItemList: DiagnosticsItem[],
	error?: DiagnosticsError,
}

export interface DiagnosticsItem {
	message: string,
	severity: string,
	line: number,
	start: number,
	end: number
}

export interface DiagnosticsError {
	message: string
}