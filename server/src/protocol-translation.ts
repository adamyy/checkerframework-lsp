import URI from "vscode-uri";
import { isWindows } from './utils'

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