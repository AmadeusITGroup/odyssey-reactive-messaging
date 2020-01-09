package com.amadeus.middleware.odyssey.reactive.messaging.core;

import java.util.concurrent.CompletableFuture;

import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.cdi.CDIMessageBuilderImpl;

@MessageScoped
public interface Message<T> {

  Iterable<MessageContext> getContexts();

  <C extends MessageContext> C getContext(String key);

  boolean hasContext(String key);

  boolean hasMergeableContext(String key);

  void addContext(MessageContext ctx);

  void mergeContext(MessageContext ctx);

  T getPayload();

  void setPayload(T payload);

  CompletableFuture<Void> getMessageAck();

  CompletableFuture<Void> getStagedAck();

  CompletableFuture<Void> getAndSetStagedAck(CompletableFuture<Void> acknowledger);

  @SuppressWarnings("unchecked")
  static <B> MessageBuilder<B> builder() {
    return new CDIMessageBuilderImpl();
  }
}
