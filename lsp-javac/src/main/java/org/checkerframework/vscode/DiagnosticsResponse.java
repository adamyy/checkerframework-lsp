package org.checkerframework.vscode;

import com.google.gson.Gson;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.util.ArrayList;
import java.util.List;

class DiagnosticsResponse {
  final String uri;
  final boolean compiledWithoutError;
  final List<DiagnosticsItem> diagnosticsItemList;

  private DiagnosticsResponse(String uri, boolean compiledWithoutError, List<DiagnosticsItem> list) {
    this.uri = uri;
    this.compiledWithoutError = compiledWithoutError;
    this.diagnosticsItemList = list;
  }

  String toJson() {
    return new Gson().toJson(this);
  }

  static DiagnosticsResponse fromJavaDiagnostics(String uri, boolean compiledWithoutError, List<Diagnostic<? extends JavaFileObject>> diagnostics) {
    List<DiagnosticsItem> diagnosticsList = new ArrayList<>();
    for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics) {
      diagnosticsList.add(DiagnosticsItem.fromJavaDiagnostic(diagnostic));
    }
    return new DiagnosticsResponse(uri, compiledWithoutError, diagnosticsList);
  }
}
