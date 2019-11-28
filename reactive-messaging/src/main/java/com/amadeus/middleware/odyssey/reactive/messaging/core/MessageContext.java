package com.amadeus.middleware.odyssey.reactive.messaging.core;

public interface MessageContext {

  /**
   * Merge this instance and the given MessageContext into a new single instance.
   * The dynamic instances should be of the same type.
   * @param messageContexts
   * @return
   */
  MessageContext merge(MessageContext... messageContexts);

  /**
   * Concrete MessageContext should return a key unique to its implementation. This key will be used to lookup
   * instance from the Message.
   * @return
   */
  String getIdentifyingKey();
}
