package com.amadeus.middleware.odyssey.reactive.messaging.core.impl;

// This is a store for global stuff
public class ReactiveMessagingContext {

  private static MessageContextFactory messageContextFactory;

  public static void setMessageContextFactory(MessageContextFactory messageContextFactory) {
    ReactiveMessagingContext.messageContextFactory = messageContextFactory;
  }

  // Returns directly the Set used use to store the instance: don't modify it
  public static MessageContextFactory getMessageContextFactory() {
    return messageContextFactory;
  }
}
