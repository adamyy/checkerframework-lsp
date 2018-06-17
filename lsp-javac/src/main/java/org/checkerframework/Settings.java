package org.checkerframework;

import com.google.gson.annotations.SerializedName;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

class Settings {
  @SerializedName("checker-framework")
  CFConfig config;

  public static class CFConfig {
    static final CFConfig DEFAULT = new CFConfig("", new ArrayList<>(), new ArrayList<>());

    final String frameworkPath;
    final List<String> checkers;
    final List<String> commandLineOptions;

    CFConfig(String frameworkPath, List<String> checkers, List<String> commandLineOptions) {
      this.frameworkPath = frameworkPath;
      this.checkers = checkers;
      this.commandLineOptions = commandLineOptions;
    }

    String jdkPath() {
      return Paths.get(frameworkPath, "checker", "dist", "jdk8.jar").toString();
    }

    String checkerPath() {
      return Paths.get(frameworkPath, "checker", "dist", "checker.jar").toString();
    }

    @Override
    public String toString() {
      return "CFConfig{" +
          "frameworkPath='" + frameworkPath + '\'' +
          ", checkers=" + checkers +
          ", commandLineOptions=" + commandLineOptions +
          '}';
    }
  }
}

