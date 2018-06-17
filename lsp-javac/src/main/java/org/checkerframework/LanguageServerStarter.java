package org.checkerframework;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;

import java.util.logging.Level;

public class LanguageServerStarter {

  public static void main(String[] args) {
    try {
      CFLanguageServer server = new CFLanguageServer();
      Launcher<LanguageClient> launcher = LSPLauncher.createServerLauncher(server, System.in, System.out);
      server.connect(launcher.getRemoteProxy());
      launcher.startListening();
    } catch (Throwable t) {
      Log.getLogger().log(Level.SEVERE, t.getMessage(), t);
      System.exit(1);
    }
  }
}
