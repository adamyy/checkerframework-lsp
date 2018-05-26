package org.checkerframework.vscode;

class DiagnosticsRequest {
  final String uri;
  final String[] checkers;
  final String filePath;
  final String jarPath;

  private DiagnosticsRequest(String uri, String[] checkers, String filePath, String jarPath) {
    this.uri = uri;
    this.checkers = checkers;
    this.filePath = filePath;
    this.jarPath = jarPath;
  }

  static DiagnosticsRequest fromString(String line) {
    final String[] configs = line.split(",");
    final String uri = configs[0].trim();
    final String[] checkers = configs[1].trim().split(":");
    final String filePath = configs[2].trim();
    final String jarPath = configs[3];
    return new DiagnosticsRequest(uri, checkers, filePath, jarPath);
  }
}
