package com.zarbosoft.semicompiled.html;

import org.unbescape.html.HtmlEscape;

import java.util.stream.Stream;

public class Text implements Piece {
  private final String text;

  public Text(String text) {
    this.text = text;
  }

  @Override
  public Stream<String> build() {
    return Stream.of(HtmlEscape.escapeHtml5(text));
  }
}
