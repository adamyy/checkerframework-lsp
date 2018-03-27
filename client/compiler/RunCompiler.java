import javax.tools.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.checkerframework.framework.util.PluginUtil;

import com.google.gson.Gson;

public class RunCompiler {
    public static void main(String[] args) {
        final BufferedWriter log = new BufferedWriter(new OutputStreamWriter(System.out));
        final BufferedReader br = new BufferedReader((new InputStreamReader(System.in)));
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        while (true) {
            try {
                String[] configs = br.readLine().split(",");
                String uri = configs[0].trim();
                String[] checkers = configs[1].trim().split(":");
                String filePath = configs[2].trim();
                String jarPath = configs[3];

                final StringWriter javacOutput = new StringWriter();
                final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
                final StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
                final List<String> options = new ArrayList<String>();

                Iterable<? extends JavaFileObject> javaFiles = fileManager.getJavaFileObjects(filePath);
                options.add("-Xbootclasspath/p:"+jarPath);
                options.add("-processor");
                options.add(PluginUtil.join(",", checkers));
//                for (String checker : checkers){
//                    options.add(checker);
//                }

                JavaCompiler.CompilationTask task = compiler
                        .getTask(javacOutput, fileManager, diagnostics, options, new ArrayList<String>(), javaFiles);

                Boolean compiledWithoutError = task.call();
                if (compiledWithoutError == false) {
                    List<Diagnostic<? extends JavaFileObject>> d = diagnostics.getDiagnostics();
                    String result = constructDiagnostics(uri, diagnostics.getDiagnostics());

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

    static class DiagnosticsObject {
        public String message, severity;
        public long line, start, end;
        public DiagnosticsObject(String message, String severity, long line, long start, long end){
            this.message = message;
            this.severity = severity;
            this.line = line;
            this.start = start;
            this.end = end;
        }
    }

    static class DiagnosticsJson {
        public String uri;
        public int count;
        public DiagnosticsObject[] list;
        public DiagnosticsJson(String uri, int count, DiagnosticsObject[] list) {
            this.uri = uri;
            this.count = count;
            this.list = list;
        }
    }

    private static String constructDiagnostics(String uri, List<Diagnostic<? extends JavaFileObject>> diagnostics) {
        int errorCount = diagnostics.size();
        DiagnosticsObject[] diagnosticsList = new DiagnosticsObject[errorCount];
        for (int i=0 ; i<errorCount; i++){
            Diagnostic obj = diagnostics.get(i);
            String message = obj.getMessage(Locale.ENGLISH);
            String severity = obj.getKind().toString();
            long line = obj.getLineNumber();
            long start = obj.getColumnNumber();
            long end = start + obj.getEndPosition() - obj.getStartPosition();
            diagnosticsList[i] = new DiagnosticsObject(message,severity,line,start,end);
        }

        Gson gson = new Gson();
        DiagnosticsJson json = new DiagnosticsJson(uri, errorCount, diagnosticsList);
        return gson.toJson(json);
    }
}
