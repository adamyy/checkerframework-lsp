package org.checkerframework.vscode;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.util.Locale;

class DiagnosticsItem {
  final String message;
  final String severity;
  final long line;
  final long start;
  final long end;

  private DiagnosticsItem(String message, String severity, long line, long start, long end){
    this.message = message;
    this.severity = severity;
    this.line = line;
    this.start = start;
    this.end = end;
  }

  static DiagnosticsItem fromJavaDiagnostic(Diagnostic<? extends JavaFileObject> javacDiagnostic) {
    String message = javacDiagnostic.getMessage(Locale.ENGLISH);
    String severity = javacDiagnostic.getKind().toString();
    long line = javacDiagnostic.getLineNumber();
    long start = javacDiagnostic.getColumnNumber();
    long end = start + javacDiagnostic.getEndPosition() - javacDiagnostic.getStartPosition();
    return new DiagnosticsItem(message, severity, line, start, end);
  }
}
