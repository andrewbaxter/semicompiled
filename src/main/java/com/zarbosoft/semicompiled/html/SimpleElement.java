package com.zarbosoft.semicompiled.html;

import java.util.stream.Stream;

public class SimpleElement extends Element<SimpleElement> {
  public SimpleElement(String tag) {
    super(tag);
  }

  @Override
  public Stream<String> build() {
    return Stream.of(String.format("<%s%s />", tag, buildAttributes()));
  }
}
