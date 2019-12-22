package com.amadeus.middleware.odyssey.reactive.messaging.core.impl;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Async;

public class DirectAsync<T> implements Async<T> {
  private T object;
  public DirectAsync(T object) {
    this.object = object;
  }
  @Override
  public T get() {
    return object;
  }
}
