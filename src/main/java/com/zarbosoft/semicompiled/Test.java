package com.zarbosoft.semicompiled;

import com.zarbosoft.semicompiled.html.Piece;

import java.util.LinkedHashMap;
import java.util.List;

public class Test {
  // File name -> output type -> (syntax name, output)
  public final LinkedHashMap<String, LinkedHashMap<String, ROPair<String, String>>> files;
  public final String id;
  public final String title;
  public final List<Piece> notes;

  public Test(String id, String title, List<Piece> notes) {
    this.id = id;
    this.title = title;
    this.notes = notes;
    this.files = new LinkedHashMap<>();
  }
}
