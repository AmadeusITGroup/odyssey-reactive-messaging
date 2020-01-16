package com.amadeus.middleware.odyssey.reactive.messaging.core;

import java.util.concurrent.CompletableFuture;

import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.cdi.CDIMessageBuilderImpl;

@MessageScoped
public interface Message<T> {

  Iterable<Metadata> getMetadata();

  <C extends Metadata> C getMetadata(String key);

  boolean hasMetadata(String key);

  boolean hasMergeableMetadata(String key);

  void addMetadata(Metadata ctx);

  void mergeMetadata(Metadata ctx);

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
