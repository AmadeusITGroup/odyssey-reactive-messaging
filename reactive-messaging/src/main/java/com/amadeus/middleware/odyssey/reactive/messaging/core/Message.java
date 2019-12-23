package com.amadeus.middleware.odyssey.reactive.messaging.core;

import java.util.concurrent.CompletableFuture;

import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.MessageBuilderProviderProvider;

@MessageScoped
public interface Message<T> {

  Iterable<MessageContext> getContexts();

  <C extends MessageContext> C getMessageContext(String key);

  void addContext(MessageContext ctx);

  T getPayload();

  void setPayload(T payload);

  CompletableFuture<Void> getMessageAck();

  CompletableFuture<Void> getStagedAck();

  CompletableFuture<Void> getAndSetStagedAck(CompletableFuture<Void> acknowledger);

  @SuppressWarnings("unchecked")
  static <B> MessageBuilder<B> builder() {
    return MessageBuilderProviderProvider.create();
  }
}
