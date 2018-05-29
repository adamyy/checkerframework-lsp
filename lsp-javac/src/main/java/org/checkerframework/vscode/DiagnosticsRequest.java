package org.checkerframework.vscode;

class DiagnosticsRequest {
  final String uri;
  final String[] checkers;
  final String filePath;
  final String jarPath;
  final String outputDir;

  private DiagnosticsRequest(String uri, String[] checkers, String filePath, String jarPath, String outputDir) {
    this.uri = uri;
    this.checkers = checkers;
    this.filePath = filePath;
    this.jarPath = jarPath;
    this.outputDir = outputDir;
  }
}
