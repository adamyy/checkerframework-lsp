package org.checkerframework;

import org.checkerframework.Settings.CFConfig;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class CFLanguageServer implements LanguageServer, LanguageClientAware {
  private final CompletableFuture<LanguageClient> client;
  private final CFTextDocumentService textDocumentService;
  private final CFWorkspaceService workspaceService;
  private Path workspaceRoot;

  public CFLanguageServer() {
    this.client = new CompletableFuture<>();
    this.textDocumentService = new CFTextDocumentService(this);
    this.workspaceService = new CFWorkspaceService(this);
  }

  @Override
  public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
    workspaceRoot = Paths.get(URI.create(params.getRootUri()).getPath());

    InitializeResult result = new InitializeResult();
    result.setCapabilities(getServerCapabilities());
    return CompletableFuture.completedFuture(result);
  }

  private ServerCapabilities getServerCapabilities() {
    ServerCapabilities capabilities = new ServerCapabilities();
    capabilities.setTextDocumentSync(TextDocumentSyncKind.Full);
    return capabilities;
  }

  @Override
  public CompletableFuture<Object> shutdown() {
    return CompletableFuture.completedFuture(null);
  }

  @Override
  public void exit() {
    // no-op
  }

  @Override
  public CFTextDocumentService getTextDocumentService() {
    return textDocumentService;
  }

  @Override
  public CFWorkspaceService getWorkspaceService() {
    return workspaceService;
  }

  @Override
  public void connect(LanguageClient client) {
    this.client.complete(client);
    Log.getLogger().addHandler(new Handler() {
      @Override
      public void publish(LogRecord record) {
        String message = record.getMessage();
        if (record.getThrown() != null) {
          StringWriter trace = new StringWriter();
          record.getThrown().printStackTrace(new PrintWriter(trace));
          message += "\n" + trace;
        }
        client.logMessage(new MessageParams(messageType(record.getLevel()), message));
      }

      private MessageType messageType(Level level) {
        if (level.intValue() >= Level.SEVERE.intValue()) return MessageType.Error;
        if (level.intValue() >= Level.WARNING.intValue()) return MessageType.Warning;
        if (level.intValue() >= Level.INFO.intValue()) return MessageType.Info;
        return MessageType.Log;
      }

      @Override
      public void flush() {
        // no-op
      }

      @Override
      public void close() throws SecurityException {
        // no-op
      }
    });
  }

  public void didChangeConfiguration(CFConfig config) {
    textDocumentService.didChangeConfiguration(config);
  }

  public void publishDiagnostics(PublishDiagnosticsParams params) {
    client.join().publishDiagnostics(params);
  }

  public void showMessage(MessageParams params) {
    client.join().showMessage(params);
  }

  public Path getWorkspaceRoot() {
    return workspaceRoot;
  }
}
