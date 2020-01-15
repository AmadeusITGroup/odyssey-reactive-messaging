package com.amadeus.middleware.odyssey.reactive.messaging.quarkus.rm;

public class AsyncCreatorImpl implements AsyncCreator {
  @Override
  public Object create(String className) {
    return new QuarkusAsync<>(className);
  }
}
