package com.amadeus.middleware.odyssey.reactive.messaging.core;

public interface MessageBuilder<T> {
  Message<T> build();

  MessageBuilder<T> fromParent(Message<?>... parents);

  MessageBuilder<T> payload(T payload);

  MessageBuilder<T> addMessageContext(MessageContext messageContext);

  /**
   * This is forcing the enabling/disabling of dependency injection activation for the message.
   */
  MessageBuilder<T> dependencyInjection(boolean activate);
}
