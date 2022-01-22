package com.zarbosoft.semicompiled.html;

import com.zarbosoft.semicompiled.ROPair;
import org.unbescape.html.HtmlEscape;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class Element<T extends Element> implements Piece {
  public static String indent = "    ";
  public final String tag;
  private final List<ROPair<String, String>> attributes = new ArrayList<>();

  public Element(String tag) {
    this.tag = tag;
  }

  public static ContainerElement container(String tag) {
    return new ContainerElement(tag);
  }

  public static SimpleElement simple(String tag) {
    return new SimpleElement(tag);
  }

  public static TextElement text(String tag) {
    return new TextElement(tag);
  }

  public static TextElement h1() {
    return new TextElement("h1");
  }

  public static TextElement h2() {
    return new TextElement("h2");
  }

  public static TextElement p() {
    return new TextElement("p");
  }

  public static ContainerElement div() {
    return new ContainerElement("div");
  }

  public T att(String key, String value) {
    attributes.add(new ROPair<>(key, value));
    return (T) this;
  }

  public T att(String key) {
    attributes.add(new ROPair<>(key, null));
    return (T) this;
  }

  protected String buildAttributes() {
    return attributes.stream()
        .map(
            a ->
                a.second == null
                    ? String.format(" %s", a.first)
                    : String.format(" %s=\"%s\"", a.first, HtmlEscape.escapeHtml5(a.second)))
        .collect(Collectors.joining());
  }

  public T r(Consumer<T> inner) {
    inner.accept((T) this);
    return (T) this;
  }
}
