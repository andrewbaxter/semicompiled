package com.zarbosoft.semicompiled;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Tempdir implements AutoCloseable {
  public final Path path;

  public Tempdir(String prefix) {
    try {
      path = Files.createTempDirectory(prefix);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void close() throws Exception {
    /*
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
           public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
             Files.delete(dir);
             return FileVisitResult.CONTINUE;
           }
         });
    */
  }
}
