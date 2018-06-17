package org.checkerframework;

import java.util.logging.Logger;

class Log {
  private static final String NAME = "Checker-Framework-Main";

  static Logger getLogger() {
    return Logger.getLogger(NAME);
  }
}
