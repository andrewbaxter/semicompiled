package com.zarbosoft.semicompiled;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
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

  public static void run(ListBuilder<String> command) {
    try {
      final ProcessBuilder builder = new ProcessBuilder(command.toList());
      builder.inheritIO();
      builder.redirectError(ProcessBuilder.Redirect.PIPE);
      final Process proc = builder.start();
      final Threads.Future<String> error =
          Threads.free(
              () -> {
                try (InputStream s = proc.getErrorStream()) {
                  return new String(s.readAllBytes(), StandardCharsets.UTF_8);
                }
              });
      if (proc.waitFor() != 0)
        throw new RuntimeException(String.format("Failed %s\n%s", command.toList(), error.get()));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void run(String... command) {
    run(ListBuilder.of(command));
  }

  public static String runOutput(ListBuilder<String> command) {
    try {
      final ProcessBuilder builder = new ProcessBuilder(command.toList());
      builder.inheritIO();
      builder.redirectError(ProcessBuilder.Redirect.PIPE);
      builder.redirectOutput(ProcessBuilder.Redirect.PIPE);
      final Process proc = builder.start();
      final Threads.Future<String> error =
          Threads.free(
              () -> {
                try (InputStream s = proc.getErrorStream()) {
                  return new String(s.readAllBytes(), StandardCharsets.UTF_8);
                }
              });
      String out;
      try (InputStream s = proc.getInputStream()) {
        out = new String(s.readAllBytes(), StandardCharsets.UTF_8);
      }
      if (proc.waitFor() != 0)
        throw new RuntimeException(String.format("Failed %s\n%s", command.toList(), error.get()));
      return out;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void recursiveDelete(Path path) {
    try {
      Files.walkFileTree(
          path,
          new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                throws IOException {
              Files.delete(file);
              return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                throws IOException {
              Files.delete(dir);
              return FileVisitResult.CONTINUE;
            }
          });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static String runOutput(String... command) {
    return runOutput(ListBuilder.of(command));
  }
}
