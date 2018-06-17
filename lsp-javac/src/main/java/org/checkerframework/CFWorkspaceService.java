package org.checkerframework;

import com.google.gson.Gson;
import org.checkerframework.Settings.CFConfig;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.WorkspaceService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class CFWorkspaceService implements WorkspaceService {
  private final Gson gson;
  private final CFLanguageServer server;
  private CFConfig config;

  public CFWorkspaceService(CFLanguageServer server) {
    this.gson = new Gson();
    this.server = server;
    this.config = CFConfig.DEFAULT;
  }

  @Override
  public void didChangeConfiguration(DidChangeConfigurationParams params) {
    config = gson.fromJson(gson.toJson(params.getSettings()), Settings.class).config;
    server.didChangeConfiguration(config);
  }

  @Override
  public void didChangeWatchedFiles(DidChangeWatchedFilesParams params) {
    // TODO
  }

  @Override
  public void didChangeWorkspaceFolders(DidChangeWorkspaceFoldersParams params) {
    Log.getLogger().log(Level.INFO, params.toString());
  }

  @Override
  public CompletableFuture<List<? extends SymbolInformation>> symbol(WorkspaceSymbolParams params) {
    return null;
  }
}
