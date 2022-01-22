package com.zarbosoft.semicompiled.html;

import com.zarbosoft.semicompiled.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ContainerElement extends Element<ContainerElement> {
  private final List<Piece> children = new ArrayList<>();

  public ContainerElement(String tag) {
    super(tag);
  }

  public ContainerElement child(Piece piece) {
    children.add(piece);
    return this;
  }

  public ContainerElement childN(Stream<Piece> pieces) {
    pieces.forEachOrdered(p -> child(p));
    return this;
  }

  public ContainerElement childNSep(Stream<Piece> pieces) {
    final List<Piece> pieces1 = pieces.collect(Collectors.toList());
    for (int i = 0; i < pieces1.size(); i++) {
      final Piece piece = pieces1.get(i);
      if (i > 0) child(Element.div().att("class", "sep"));
      child(piece);
    }
    return this;
  }

  @Override
  public Stream<String> build() {
    return Utils.streamCat(
        Stream.of(String.format("<%s%s>", tag, buildAttributes())),
        children.stream().flatMap(c -> c.build()),
        Stream.of(String.format("</%s>", tag)));
  }
}
