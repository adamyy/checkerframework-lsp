package org.checkerframework;

import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

import javax.tools.JavaFileObject;

class Utils {
  private static final String SOURCE_NAME = "checker-framework";

  static boolean validLspDiagnostic(javax.tools.Diagnostic<? extends JavaFileObject> diagnostic) {
    return diagnostic.getStartPosition() != javax.tools.Diagnostic.NOPOS;
  }

  static DiagnosticSeverity toLspSeverity(javax.tools.Diagnostic.Kind kind) {
    switch (kind) {
      case ERROR:
        return DiagnosticSeverity.Error;
      case WARNING:
      case MANDATORY_WARNING:
        return DiagnosticSeverity.Warning;
      case NOTE:
      case OTHER:
      default:
        return DiagnosticSeverity.Information;
    }
  }

  static Diagnostic toLspDiagnostic(javax.tools.Diagnostic<? extends JavaFileObject> javacDiagnostic) {
    return new Diagnostic(
        new Range(
            new Position(
                (int) (javacDiagnostic.getLineNumber() - 1),
                (int) (javacDiagnostic.getColumnNumber() - 1)
            ),
            new Position(
                (int) (javacDiagnostic.getLineNumber() - 1),
                (int) (javacDiagnostic.getColumnNumber() + javacDiagnostic.getEndPosition() - javacDiagnostic.getStartPosition() - 1)
            )
        ),
        javacDiagnostic.getMessage(null),
        toLspSeverity(javacDiagnostic.getKind()),
        SOURCE_NAME,
        javacDiagnostic.getCode()
    );
  }
}
