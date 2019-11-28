package com.amadeus.middleware.odyssey.reactive.messaging.core;

public interface MessageBuilder {
  Message build();

  MessageBuilder fromParent(Message<?>... parents);

  MessageBuilder payload(Object payload);

  MessageBuilder addMessageContext(MessageContext messageContext);

  /**
   * This is forcing the enabling/disabling of dependency injection activation for the message.
   */
  MessageBuilder dependencyInjection(boolean activate);
}
