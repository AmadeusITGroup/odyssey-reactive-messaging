package com.amadeus.middleware.odyssey.reactive.messaging.core.impl;

// This is a store for global stuff
public class ReactiveMessagingContext {

  private static MessageInitializerRegistry messageInitializerRegistry;

  public static void setMessageInitializerRegistry(MessageInitializerRegistry messageInitializerRegistry) {
    ReactiveMessagingContext.messageInitializerRegistry = messageInitializerRegistry;
  }

  // Returns directly the Set used use to store the instance: don't modify it
  public static MessageInitializerRegistry getMessageInitializerRegistry() {
    return messageInitializerRegistry;
  }
}
