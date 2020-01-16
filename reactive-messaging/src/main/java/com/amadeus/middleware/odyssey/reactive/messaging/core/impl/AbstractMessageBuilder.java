package com.amadeus.middleware.odyssey.reactive.messaging.core.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageBuilder;
import com.amadeus.middleware.odyssey.reactive.messaging.core.Metadata;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MutableMetadata;

public abstract class AbstractMessageBuilder<T> implements MessageBuilder<T> {

  protected boolean dependencyInjection = true; // TODO: enable "global" configuration

  protected List<Message<?>> parents = new ArrayList<>();
  protected List<Metadata> metadata = new ArrayList<>();
  protected T payload;

  /**
   * Warning: Metadatas are not copied from the parent at this time.
   * 
   * @param parents
   * @return
   */
  @Override
  public MessageBuilder<T> fromParents(Message<?>... parents) {
    this.parents.addAll(Arrays.asList(parents));
    return this;
  }

  @Override
  public MessageBuilder<T> payload(T payload) {
    this.payload = payload;
    return this;
  }

  @Override
  public MessageBuilder<T> addMetadata(Metadata metadata) {
    this.metadata.add(metadata);
    return this;
  }

  @Override
  public MessageBuilder<T> dependencyInjection(boolean activate) {
    dependencyInjection = activate;
    return this;
  }

  protected static void setupParentChildLink(List<Message<?>> parents, Message<?> child) {
    if (parents.isEmpty()) {
      return;
    }

    for (Message<?> parent : parents) {
      mergeMetadatas(parent, child);
      setAcknowledgmentLink(parent, child);
    }
  }

  private static void setAcknowledgmentLink(Message<?> parent, Message<?> child) {
    CompletableFuture<Void> newParentStagedAck = new CompletableFuture<>();
    CompletableFuture<Void> previousParentStagedAck = parent.getAndSetStagedAck(newParentStagedAck);
    CompletableFuture<Void> merged = CompletableFuture.allOf(newParentStagedAck, child.getMessageAck());
    CompletableFutureUtils.propagate(merged, previousParentStagedAck);
  }

  private static void mergeMetadatas(Message<?> parent, Message<?> child) {
    for (Metadata metadata : parent.getMetadata()) {
      if (!metadata.isMetadataPropagable()) {
        continue;
      }
      Metadata metadataToMerge;
      if (MutableMetadata.class.isAssignableFrom(metadata.getClass())) {
        MutableMetadata mmc = (MutableMetadata) metadata;
        metadataToMerge = mmc.createChild();
      } else {
        metadataToMerge = metadata;
      }
      child.mergeMetadata(metadataToMerge);
    }
  }
}
