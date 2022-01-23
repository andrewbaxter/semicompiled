package com.zarbosoft.semicompiled;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ListBuilder<T> {
  private final List<T> inner = new ArrayList<>();

  public static <T> ListBuilder<T> of(T... values) {
    return new ListBuilder<T>().add(values);
  }

  public ListBuilder<T> add(T... values) {
    for (T value : values) {
      inner.add(value);
    }
    return this;
  }

  public ListBuilder<T> addStream(Stream<T> values) {
    values.forEachOrdered(v -> add(v));
    return this;
  }

  public ListBuilder<T> addIf(boolean cond, T... values) {
    if (cond) add(values);
    return this;
  }

  public List<T> toList() {
    return inner;
  }
}
