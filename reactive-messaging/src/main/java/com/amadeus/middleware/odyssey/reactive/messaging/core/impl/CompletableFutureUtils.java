package com.amadeus.middleware.odyssey.reactive.messaging.core.impl;

import java.util.concurrent.CompletableFuture;

public class CompletableFutureUtils {

  private CompletableFutureUtils() {
  }

  /**
   * Propagate the completion state from a CompletableFuture to Another one.
   */
  public static <T> void propagate(CompletableFuture<T> from, CompletableFuture<T> to) {
    from.whenComplete((t, e) -> {
      if (e != null) {
        to.completeExceptionally(e);
      } else {
        to.complete(t);
      }
    });
  }
}
