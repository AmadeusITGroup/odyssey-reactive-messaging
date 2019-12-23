package com.amadeus.middleware.odyssey.reactive.messaging.core.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import javax.enterprise.inject.Vetoed;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageContext;

@Vetoed
public class MessageImpl<T> implements Message<T> {

  private Long scopeContextId;

  private List<MessageContext> messageContexts;
  private T payload;

  private CompletableFuture<Void> messageAck = new CompletableFuture<>();

  private AtomicReference<CompletableFuture<Void>> stageAck = new AtomicReference<>(messageAck);

  public MessageImpl(List<MessageContext> messageContexts, T payload) {
    this.messageContexts = messageContexts;
    this.payload = payload;
  }

  public void setScopeContextId(Long scopeContextId) {
    this.scopeContextId = scopeContextId;
  }

  public Long getScopeContextId() {
    return scopeContextId;
  }

  @Override
  public Iterable<MessageContext> getContexts() {
    return new ArrayList<>(messageContexts);
  }

  @Override
  public void addContext(MessageContext ctx) {
    if (ctx == null) {
      return;
    }
    messageContexts.add(ctx);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <C extends MessageContext> C getMessageContext(String key) {
    for (MessageContext mc : getContexts()) {
      if (mc.getIdentifyingKey()
          .equals(key)) {
        return (C) mc;
      }
    }
    return null;
  }

  @Override
  public T getPayload() {
    return payload;
  }

  @Override
  public void setPayload(T payload) {
    this.payload = payload;
  }

  @Override
  public CompletableFuture<Void> getMessageAck() {
    return messageAck;
  }

  @Override
  public CompletableFuture<Void> getStagedAck() {
    return stageAck.get();
  }

  @Override
  public CompletableFuture<Void> getAndSetStagedAck(CompletableFuture<Void> acknowledger) {
    return this.stageAck.getAndSet(acknowledger);
  }

  @Override
  public String toString() {
    return "{" + "messageContexts=" + messageContexts + ", payload=" + payload + '}';
  }
}
