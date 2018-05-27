package org.checkerframework.vscode;

import org.checkerframework.checker.nullness.qual.NonNull;

class DiagnosticsError {
  @NonNull final String message;

  private DiagnosticsError(String message) {
    this.message = message;
  }

  static DiagnosticsError fromThrowable(Throwable throwable) {
    return new DiagnosticsError(throwable.toString());
  }
}
