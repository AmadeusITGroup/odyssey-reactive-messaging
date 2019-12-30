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

  /**
   * Get for a message instance and a type the associated value.
   *
   * The type can be <code>Message</code>, the payload type, or a <code>MessageContext</code>.
   */
  @SuppressWarnings("unchecked")
  public static <T> T get(Message message, Class<T> clazz) {
    if (message == null) {
      return null;
    }
    if (Message.class.isAssignableFrom(clazz)) {
      return (T) message;
    }
    Object payload = message.getPayload();
    if ((payload != null) && (payload.getClass()
      .isAssignableFrom(clazz))) {
      return (T) payload;
    }
    if (MessageContext.class.isAssignableFrom(clazz)) {
      Iterable<MessageContext> it = message.getContexts();
      for (MessageContext mc : it) {
        if (clazz.isAssignableFrom(mc.getClass())) {
          return (T) mc;
        }
      }
    }
    return null;
  }
}
