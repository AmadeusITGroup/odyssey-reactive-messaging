package com.amadeus.middleware.odyssey.reactive.messaging.quarkus.rm;

public interface AsyncCreator {
  Object create(String className);
}
