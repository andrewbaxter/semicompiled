package com.zarbosoft.semicompiled.html;

import org.unbescape.html.HtmlEscape;

import java.util.stream.Stream;

public class TextElement extends Element<TextElement> {
  private String text = "";

  public TextElement(String tag) {
    super(tag);
  }

  public TextElement t(String text) {
    this.text = this.text + text;
    return this;
  }

  public TextElement tf(String format, Object... args) {
    this.text = this.text + String.format(format, args);
    return this;
  }

  @Override
  public Stream<String> build() {
    return Stream.of(
        String.format("<%s%s>%s</%s>", tag, buildAttributes(), HtmlEscape.escapeHtml5(text), tag));
  }
}
