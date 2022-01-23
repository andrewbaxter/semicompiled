package com.zarbosoft.semicompiled.html;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InlineElement extends Element<InlineElement> {
  private final List<Piece> children = new ArrayList<>();

  public InlineElement(String tag) {
    super(tag);
  }

  public InlineElement t(String text) {
    child(new Text(text));
    return this;
  }

  public InlineElement child(Piece piece) {
    children.add(piece);
    return this;
  }

  public InlineElement childN(Stream<Piece> pieces) {
    pieces.forEachOrdered(p -> child(p));
    return this;
  }

  @Override
  public Stream<String> build() {
    return Stream.of(
        String.format(
            "<%s%s>%s</%s>",
            tag,
            buildAttributes(),
            children.stream().flatMap(p -> p.build()).collect(Collectors.joining("")),
            tag));
  }

  public Piece tf(String format, Object... args) {
    child(new Text(String.format(format, args)));
    return this;
  }
}
