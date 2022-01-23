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

  public void manualClean() {
    Utils.recursiveDelete(path);
  }

  @Override
  public void close() throws Exception {
    /*
     Utils.recursiveDelete(path);
    */
  }
}
