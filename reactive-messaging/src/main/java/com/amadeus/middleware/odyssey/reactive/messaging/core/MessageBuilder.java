package com.amadeus.middleware.odyssey.reactive.messaging.core;

public interface MessageBuilder<T> {
  Message<T> build();

  MessageBuilder<T> fromParents(Message<?>... parents);

  MessageBuilder<T> payload(T payload);

  MessageBuilder<T> addMetadata(Metadata metadata);

  /**
   * This is forcing the enabling/disabling of dependency injection activation for the message.
   */
  MessageBuilder<T> dependencyInjection(boolean activate);
}
