export function isWindows(): boolean {
    return /^win/.test(process.platform);
}