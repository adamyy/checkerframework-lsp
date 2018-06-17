package org.checkerframework;

import org.checkerframework.Settings.CFConfig;

import javax.tools.*;
import javax.tools.JavaCompiler.CompilationTask;
import java.io.File;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Javac {
  private final CFConfig config;
  private final JavaCompiler compiler;
  private final StandardJavaFileManager fileManager;

  Javac(CFConfig config) {
    this.config = config;
    this.compiler = ToolProvider.getSystemJavaCompiler();
    this.fileManager = compiler.getStandardFileManager(null, null, Charset.defaultCharset());
  }

  Map<JavaFileObject, List<Diagnostic<? extends JavaFileObject>>> compile(List<File> files) throws RuntimeException {
    if (files.isEmpty()) return Collections.emptyMap();

    Log.getLogger().log(Level.INFO, String.join(",", options()));

    final DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<>();

    final CompilationTask task = buildJavacTask(toJavaFileObjects(files), diagnosticCollector);
    task.call();

    return diagnosticCollector
        .getDiagnostics()
        .stream()
        .collect(Collectors.groupingBy(Diagnostic::getSource));
  }

  private CompilationTask buildJavacTask(
      Iterable<? extends JavaFileObject> files,
      DiagnosticListener<JavaFileObject> diagnosticListener) {
    return compiler.getTask(
        new StringWriter(),
        fileManager,
        diagnosticListener,
        options(),
        null,
        files
    );
  }

  private Iterable<? extends JavaFileObject> toJavaFileObjects(Iterable<File> files) {
    return fileManager.getJavaFileObjectsFromFiles(files);
  }

  private List<String> options() {
    List<String> cfOptions = Arrays.asList(
        "-Xbootclasspath/p:" + config.jdkPath(),
        "-processorpath",
        config.checkerPath(),
        "-proc:only",
        "-processor",
        String.join(",", config.checkers)
    );

    List<String> customOptions = config.commandLineOptions;

    return Stream.concat(cfOptions.stream(), customOptions.stream()).collect(Collectors.toList());
  }
}
