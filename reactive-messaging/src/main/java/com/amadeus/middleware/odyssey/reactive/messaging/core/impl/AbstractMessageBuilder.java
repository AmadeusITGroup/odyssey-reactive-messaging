package com.amadeus.middleware.odyssey.reactive.messaging.core.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageBuilder;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageContext;

public abstract class AbstractMessageBuilder implements MessageBuilder {

  protected boolean dependencyInjection = true; // TODO: enable "global" configuration

  protected List<Message<?>> parents = new ArrayList<>();
  protected List<MessageContext> messageContexts = new ArrayList<>();
  protected Object payload;

  /**
   * Warning: MessageContexts are not copied from the parent at this time.
   * 
   * @param parents
   * @return
   */
  @Override
  public MessageBuilder fromParent(Message<?>... parents) {
    this.parents.addAll(Arrays.asList(parents));
    return this;
  }

  @Override
  public MessageBuilder payload(Object payload) {
    this.payload = payload;
    return this;
  }

  @Override
  public MessageBuilder addMessageContext(MessageContext messageContext) {
    messageContexts.add(messageContext);
    return this;
  }

  @Override
  public MessageBuilder dependencyInjection(boolean activate) {
    dependencyInjection = activate;
    return this;
  }

  protected static void setupParentChildLink(List<Message<?>> parents, Message child) {
    if (parents.isEmpty()) {
      return;
    }

    for (Message<?> parent : parents) {
      CompletableFuture<Void> newParentStagedAck = new CompletableFuture<>();
      CompletableFuture<Void> previousParentStagedAck = parent.getAndSetStagedAck(newParentStagedAck);
      CompletableFuture<Void> merged = CompletableFuture.allOf(newParentStagedAck, child.getMessageAck());
      CompletableFutureUtils.propagate(merged, previousParentStagedAck);
    }
  }
}
