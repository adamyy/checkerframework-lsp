package org.checkerframework.vscode;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.util.Locale;

class DiagnosticsObject {
  private final String message;
  private final String severity;
  private final long line;
  private final long start;
  private final long end;

  private DiagnosticsObject(String message, String severity, long line, long start, long end){
    this.message = message;
    this.severity = severity;
    this.line = line;
    this.start = start;
    this.end = end;
  }

  static DiagnosticsObject fromJavaDiagnostic(Diagnostic<? extends JavaFileObject> javacDiagnostic) {
    String message = javacDiagnostic.getMessage(Locale.ENGLISH);
    String severity = javacDiagnostic.getKind().toString();
    long line = javacDiagnostic.getLineNumber();
    long start = javacDiagnostic.getColumnNumber();
    long end = start + javacDiagnostic.getEndPosition() - javacDiagnostic.getStartPosition();
    return new DiagnosticsObject(message, severity, line, start, end);
  }
}
