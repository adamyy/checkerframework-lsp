package org.checkerframework.sample;

import org.checkerframework.checker.nullness.qual.NonNull;

public class NullnessSample {
  public static void assignNull() {
    @NonNull String str = null;
    System.out.println(str);
  }

  public static void main(String[] args) {
    assignNull();
  }
}
