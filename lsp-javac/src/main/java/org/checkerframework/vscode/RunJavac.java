package org.checkerframework.vscode;

import com.google.gson.Gson;
import org.checkerframework.javacutil.PluginUtil;

import javax.tools.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RunJavac {
  public static void main(String[] args) {
    final Gson gson = new Gson();
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

        final DiagnosticsRequest request = gson.fromJson(line, DiagnosticsRequest.class);

        final File outputDirectory = new File(request.outputDir);
        outputDirectory.mkdirs();

        final StringWriter javacOutput = new StringWriter();
        final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        final StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        final Iterable<? extends JavaFileObject> javaFiles = fileManager.getJavaFileObjects(request.filePath);
        final List<String> options = new ArrayList<>();
        options.add("-Xbootclasspath/p:" + request.jarPath);
        options.add("-processor");
        options.add(PluginUtil.join(",", request.checkers));
        options.add("-d");
        options.add(request.outputDir);

        final JavaCompiler.CompilationTask task = compiler
            .getTask(javacOutput, fileManager, diagnostics, options, new ArrayList<>(), javaFiles);

        DiagnosticsResponse response;

        try {
          task.call();
          response = DiagnosticsResponse.fromJavaDiagnostics(request.uri, diagnostics.getDiagnostics());
        } catch (Exception e) {
          response = DiagnosticsResponse.fromThrowable(request.uri, e);
        }

        try {
          log.write(gson.toJson(response));
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
