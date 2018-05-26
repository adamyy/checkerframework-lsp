package org.checkerframework.vscode;

import com.google.gson.Gson;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Bean object for IPC with vscode server process
 */
class DiagnosticsJson {
  private static final Gson gson = new Gson();

  private final String uri;
  private final int count;
  private final List<DiagnosticsObject> list;

  private DiagnosticsJson(String uri, int count, List<DiagnosticsObject> list) {
    this.uri = uri;
    this.count = count;
    this.list = list;
  }

  private String toJson() {
    return gson.toJson(this);
  }

  static String constructDiagnosticsJson(String uri, List<Diagnostic<? extends JavaFileObject>> diagnostics) {
    List<DiagnosticsObject> diagnosticsList = new ArrayList<>();
    for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics) {
      diagnosticsList.add(DiagnosticsObject.fromJavaDiagnostic(diagnostic));
    }
    return new DiagnosticsJson(uri, diagnostics.size(), diagnosticsList).toJson();
  }
}
