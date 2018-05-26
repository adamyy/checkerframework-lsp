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
        if (line == null) continue;
        final String[] configs = line.split(",");
        final String uri = configs[0].trim();
        final String[] checkers = configs[1].trim().split(":");
        final String filePath = configs[2].trim();
        final String jarPath = configs[3];

        final StringWriter javacOutput = new StringWriter();
        final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        final StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        final List<String> options = new ArrayList<>();
        options.add("-Xbootclasspath/p:" + jarPath);
        options.add("-processor");
        options.add(PluginUtil.join(",", checkers));

        final Iterable<? extends JavaFileObject> javaFiles = fileManager.getJavaFileObjects(filePath);
        final JavaCompiler.CompilationTask task = compiler
            .getTask(javacOutput, fileManager, diagnostics, options, new ArrayList<String>(), javaFiles);

        final Boolean compiledWithoutError = task.call();
        if (!compiledWithoutError) {
          String result = DiagnosticsJson.constructDiagnosticsJson(uri, diagnostics.getDiagnostics());

          try {
            log.write(result);
            log.flush();
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
