package com.zarbosoft.semicompiled;

import com.zarbosoft.semicompiled.html.Element;

import java.util.stream.Stream;

public class Main {
  public static String capFirst(String text) {
    return text.substring(0, 1).toUpperCase() + text.substring(1);
  }

  public static void main(String[] args) {
    Generator generator = new Generator();
    generator.section(
        JavaTests.build(),
        Stream.of(
            Element.p()
                .t(
                    "Compiled with source/target version 1.8. Includes javap and ASMifier output.")));
    generator.section(
        CTests.build(), Stream.of(Element.p().t("Including LLVM IR, WASM, and eBPF.")));
    generator.build();
  }
}
