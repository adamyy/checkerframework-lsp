package org.checkerframework.vscode;

import org.checkerframework.javacutil.PluginUtil;

import javax.tools.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RunJavac {
  public static void main(String[] args) {
    final BufferedWriter log = new BufferedWriter(new OutputStreamWriter(System.out));
    final BufferedReader br = new BufferedReader((new InputStreamReader(System.in)));
    final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

    while (true) {
      try {
        final String line = br.readLine();
        if (line == null) {
          Thread.sleep(3000);
          continue;
        }

        final DiagnosticsRequest request = DiagnosticsRequest.fromString(line);
        final StringWriter javacOutput = new StringWriter();
        final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        final StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        final List<String> options = new ArrayList<>();
        options.add("-Xbootclasspath/p:" + request.jarPath);
        options.add("-processor");
        options.add(PluginUtil.join(",", request.checkers));

        final Iterable<? extends JavaFileObject> javaFiles = fileManager.getJavaFileObjects(request.filePath);
        final JavaCompiler.CompilationTask task = compiler
            .getTask(javacOutput, fileManager, diagnostics, options, new ArrayList<>(), javaFiles);
        final Boolean compiledWithoutError = task.call();

        DiagnosticsResponse response =
            DiagnosticsResponse.fromJavaDiagnostics(request.uri, compiledWithoutError, diagnostics.getDiagnostics());

        try {
          log.write(response.toJson());
          log.flush();
        } catch (Exception e) {
          e.printStackTrace();
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
