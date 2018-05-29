package org.checkerframework.vscode;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.util.ArrayList;
import java.util.List;

class DiagnosticsResponse {
  final String uri;
  final List<DiagnosticsItem> diagnosticsItemList;
  final DiagnosticsError error;

  private DiagnosticsResponse(String uri, List<DiagnosticsItem> list, DiagnosticsError error) {
    this.uri = uri;
    this.diagnosticsItemList = list;
    this.error = error;
  }

  static DiagnosticsResponse fromJavaDiagnostics(String uri, List<Diagnostic<? extends JavaFileObject>> diagnostics) {
    List<DiagnosticsItem> diagnosticsList = new ArrayList<>();
    for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics) {
      diagnosticsList.add(DiagnosticsItem.fromJavaDiagnostic(diagnostic));
    }
    return new DiagnosticsResponse(uri, diagnosticsList, null);
  }

  static DiagnosticsResponse fromThrowable(String uri, Throwable throwable) {
    return new DiagnosticsResponse(uri, new ArrayList<>(), DiagnosticsError.fromThrowable(throwable));
  }
}
