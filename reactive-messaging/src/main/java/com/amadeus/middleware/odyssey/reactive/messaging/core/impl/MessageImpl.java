package com.amadeus.middleware.odyssey.reactive.messaging.core.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import javax.enterprise.inject.Vetoed;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;
import com.amadeus.middleware.odyssey.reactive.messaging.core.Metadata;

@Vetoed
public class MessageImpl<T> implements Message<T> {

  private Long scopeContextId;

  private List<Metadata> metadata;
  private T payload;

  private CompletableFuture<Void> messageAck = new CompletableFuture<>();

  private AtomicReference<CompletableFuture<Void>> stageAck = new AtomicReference<>(messageAck);

  public MessageImpl(List<Metadata> metadata, T payload) {
    this.metadata = metadata;
    this.payload = payload;
  }

  public void setScopeContextId(Long scopeContextId) {
    this.scopeContextId = scopeContextId;
  }

  public Long getScopeContextId() {
    return scopeContextId;
  }

  @Override
  public Iterable<Metadata> getMetadata() {
    return new ArrayList<>(metadata);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <C extends Metadata> C getMetadata(String key) {
    for (Metadata mc : getMetadata()) {
      if (mc.getMetadataKey()
          .equals(key)) {
        return (C) mc;
      }
    }
    return null;
  }

  @Override
  public boolean hasMetadata(String key) {
    for (Metadata mc : getMetadata()) {
      if (mc.getMetadataKey()
          .equals(key)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean hasMergeableMetadata(String key) {
    for (Metadata mc : getMetadata()) {
      if (mc.getMetadataMergeKey()
          .equals(key)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void addMetadata(Metadata ctx) {
    if (ctx == null) {
      return;
    }
    mergeMetadata(ctx);
  }

  @Override
  public void mergeMetadata(Metadata ctx) {
    Iterator<Metadata> it = metadata.iterator();
    while (it.hasNext()) {
      Metadata mc = it.next();
      if (ctx.getMetadataMergeKey()
          .equals(mc.getMetadataMergeKey())) {
        Metadata merged = mc.metadataMerge(ctx);
        if (merged != mc) {
          it.remove();
          metadata.add(merged);
        }
        return;
      }
    }
    // no actual merge performed, let's simply add it
    metadata.add(ctx);
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
    return "Message{" + "metadata=" + metadata + ", payload=" + payload + '}';
  }

  /**
   * Get for a message instance and a type the associated value. The type can be <code>Message</code>, the payload type,
   * or a <code>Metadata</code>.
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
    if (Metadata.class.isAssignableFrom(clazz)) {
      Iterable<Metadata> it = message.getMetadata();
      for (Metadata mc : it) {
        if (clazz.isAssignableFrom(mc.getClass())) {
          return (T) mc;
        }
      }
    }
    return null;
  }
}
