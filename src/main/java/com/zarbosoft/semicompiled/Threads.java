package com.zarbosoft.semicompiled;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class Threads {
  private static final Threads threads = new Threads();
  private final ExecutorService executor = Executors.newWorkStealingPool();

  public static <T> Future<T> launch(Supplier<T> inner) {
    CompletableFuture<T> out = new CompletableFuture<>();
    threads.executor.execute(
        () -> {
          try {
            out.complete(inner.get());
          } catch (Throwable t) {
            out.completeExceptionally(t);
          }
        });
    return new Future<>(out);
  }

  public static void shutdown() {
    threads.executor.shutdown();
    try {
      threads.executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public static class Future<T> {
    private final CompletableFuture<T> inner;

    public Future(CompletableFuture<T> inner) {
      this.inner = inner;
    }

    public T get() {
      try {
        return inner.get();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      } catch (ExecutionException e) {
        if (e.getCause() instanceof RuntimeException) throw (RuntimeException) e.getCause();
        throw new RuntimeException(e.getCause());
      }
    }
  }
}
