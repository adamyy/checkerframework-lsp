package org.checkerframework;

import org.checkerframework.Settings.CFConfig;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.TextDocumentService;

import java.io.File;
import java.net.URI;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class CFTextDocumentService implements TextDocumentService {
  private final CFLanguageServer server;
  private final Set<URI> activeFileURIs;
  private Javac javac;

  CFTextDocumentService(CFLanguageServer server) {
    this.server = server;
    this.activeFileURIs = new HashSet<>();
  }

  @Override
  public void didOpen(DidOpenTextDocumentParams params) {
    Log.getLogger().log(Level.INFO, params.toString());
    final TextDocumentItem document = params.getTextDocument();
    final URI uri = URI.create(document.getUri());
    final File file = new File(uri);

    activeFileURIs.add(uri);

    performDiagnostic(Collections.singletonList(file));
  }

  @Override
  public void didChange(DidChangeTextDocumentParams params) {

  }

  @Override
  public void didClose(DidCloseTextDocumentParams params) {
    Log.getLogger().log(Level.INFO, params.toString());
    final TextDocumentIdentifier document = params.getTextDocument();
    final URI uri = URI.create(document.getUri());

    activeFileURIs.remove(uri);

    server.publishDiagnostics(new PublishDiagnosticsParams(document.getUri(), new ArrayList<>()));
  }

  @Override
  public void didSave(DidSaveTextDocumentParams params) {
    Log.getLogger().log(Level.INFO, params.toString());
    final TextDocumentIdentifier document = params.getTextDocument();
    final URI uri = URI.create(document.getUri());
    final File file = new File(uri);

    performDiagnostic(Collections.singletonList(file));
  }

  public void didChangeConfiguration(CFConfig config) {
    this.javac = new Javac(config);

    performDiagnostic(activeFileURIs.stream().map(uri -> new File(uri)).collect(Collectors.toList()));
  }

  public void performDiagnostic(List<File> files) {
    files.forEach(file -> Log.getLogger().log(Level.INFO, file.getPath()));

    activeFileURIs.forEach(uri -> server.publishDiagnostics(new PublishDiagnosticsParams(uri.toString(), new ArrayList<>())));

    try {
      javac.compile(files).forEach((javaFileObject, diagnostics) -> {
        Log.getLogger().log(Level.INFO,
            javaFileObject.toUri().toString());
        Log.getLogger().log(Level.INFO,
            String.join(",", diagnostics.stream().map(diagnostic -> diagnostic.getMessage(null)).collect(Collectors.toList())));
        server.publishDiagnostics(new PublishDiagnosticsParams(
            javaFileObject.toUri().toString(),
            diagnostics.stream()
                .filter(Utils::validLspDiagnostic)
                .map(Utils::toLspDiagnostic)
                .collect(Collectors.toList())
        ));
      });
    } catch (RuntimeException e) {
      Log.getLogger().log(Level.SEVERE, e.getMessage(), e);
    }
  }

  @Override
  public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(CompletionParams position) {
    return null;
  }

  @Override
  public CompletableFuture<CompletionItem> resolveCompletionItem(CompletionItem unresolved) {
    return null;
  }

  @Override
  public CompletableFuture<Hover> hover(TextDocumentPositionParams position) {
    return null;
  }

  @Override
  public CompletableFuture<SignatureHelp> signatureHelp(TextDocumentPositionParams position) {
    return null;
  }

  @Override
  public CompletableFuture<List<? extends Location>> definition(TextDocumentPositionParams position) {
    return null;
  }

  @Override
  public CompletableFuture<List<? extends Location>> references(ReferenceParams params) {
    return null;
  }

  @Override
  public CompletableFuture<List<? extends DocumentHighlight>> documentHighlight(TextDocumentPositionParams position) {
    return null;
  }

  @Override
  public CompletableFuture<List<? extends SymbolInformation>> documentSymbol(DocumentSymbolParams params) {
    return null;
  }

  @Override
  public CompletableFuture<List<? extends Command>> codeAction(CodeActionParams params) {
    return null;
  }

  @Override
  public CompletableFuture<List<? extends CodeLens>> codeLens(CodeLensParams params) {
    return null;
  }

  @Override
  public CompletableFuture<CodeLens> resolveCodeLens(CodeLens unresolved) {
    return null;
  }

  @Override
  public CompletableFuture<List<? extends TextEdit>> formatting(DocumentFormattingParams params) {
    return null;
  }

  @Override
  public CompletableFuture<List<? extends TextEdit>> rangeFormatting(DocumentRangeFormattingParams params) {
    return null;
  }

  @Override
  public CompletableFuture<List<? extends TextEdit>> onTypeFormatting(DocumentOnTypeFormattingParams params) {
    return null;
  }

  @Override
  public CompletableFuture<WorkspaceEdit> rename(RenameParams params) {
    return null;
  }
}
