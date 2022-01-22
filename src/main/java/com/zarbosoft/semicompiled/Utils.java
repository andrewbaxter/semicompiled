package com.zarbosoft.semicompiled;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class Utils {
  public static <T> Stream<T> streamCat(Stream<T>... streams) {
    return Stream.of(streams).flatMap(s -> s);
  }

  public static <T> Stream<T> streamOpt(boolean condition, Supplier<Stream<T>> present) {
    if (condition) {
      return present.get();
    } else return Stream.of();
  }
}
