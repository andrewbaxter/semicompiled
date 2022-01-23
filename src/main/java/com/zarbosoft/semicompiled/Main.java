package com.zarbosoft.semicompiled;

import java.util.stream.Stream;

public class Main {
  public static String capFirst(String text) {
    return text.substring(0, 1).toUpperCase() + text.substring(1);
  }

  public static void main(String[] args) {
    Generator generator = new Generator();
    generator.section(JavaTests.build(), Stream.of());
    generator.section(CTests.build(), Stream.of());
    generator.build();
  }
}
