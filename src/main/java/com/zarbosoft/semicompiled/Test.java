package com.zarbosoft.semicompiled;

import java.util.LinkedHashMap;

public class Test {
// File name -> output type -> (syntax name, output)
  public final LinkedHashMap<String, LinkedHashMap<String, ROPair<String, String>>> files;
  public final String id;
  public final String title;

  public Test(String id, String title) {
    this.id = id;
    this.title = title;
    this.files = new LinkedHashMap<>();
  }
}
