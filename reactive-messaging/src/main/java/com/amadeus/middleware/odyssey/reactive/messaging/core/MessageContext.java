package com.amadeus.middleware.odyssey.reactive.messaging.core;

public interface MessageContext {

  /**
   * Concrete MessageContext should return a key unique to its implementation. This key will be used to lookup instance
   * from the Message.
   * 
   * @return
   */
  String getContextKey();

  /**
   * Indicates whether this MessageContext should propagate into child Messages.
   * 
   * @return true is it should, else otherwise.
   */
  boolean isPropagable();

  /**
   * Concrete MessageContext should return a key unique that is shared by mergeable instances.
   * 
   * @return
   */
  String getContextMergeKey();

  /**
   * Merge this instance and the given MessageContext into a new single instance. The instances should be of the same
   * dynamic type.
   * 
   * @param messageContexts
   * @return
   */
  MessageContext merge(MessageContext... messageContexts);
}
