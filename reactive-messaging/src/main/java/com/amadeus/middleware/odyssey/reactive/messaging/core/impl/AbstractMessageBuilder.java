package com.amadeus.middleware.odyssey.reactive.messaging.core.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageBuilder;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageContext;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MutableMessageContext;

public abstract class AbstractMessageBuilder<T> implements MessageBuilder<T> {

  protected boolean dependencyInjection = true; // TODO: enable "global" configuration

  protected List<Message<?>> parents = new ArrayList<>();
  protected List<MessageContext> messageContexts = new ArrayList<>();
  protected T payload;

  /**
   * Warning: MessageContexts are not copied from the parent at this time.
   * 
   * @param parents
   * @return
   */
  @Override
  public MessageBuilder<T> fromParent(Message<?>... parents) {
    this.parents.addAll(Arrays.asList(parents));
    return this;
  }

  @Override
  public MessageBuilder<T> payload(T payload) {
    this.payload = payload;
    return this;
  }

  @Override
  public MessageBuilder<T> addMessageContext(MessageContext messageContext) {
    messageContexts.add(messageContext);
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

      // Creating the child MessageContexts {
      //  Obviously, this is quite limited at this stage:
      //    * It is probably not optimal regarding performance.
      //    * The merge of multi-parent MessageContext is not properly addressed so far.
      for (MessageContext messageContext : parent.getContexts()) {
        if (child.getMessageContext(messageContext.getIdentifyingKey()) != null) {
          continue;
        }
        if (MutableMessageContext.class.isAssignableFrom(messageContext.getClass())) {
          MutableMessageContext mmc = (MutableMessageContext) messageContext;
          child.addContext(mmc.createChild());
        } else {
          child.addContext(messageContext);
        }
      }
      // }

      // Setup the acknowledgement link
      CompletableFuture<Void> newParentStagedAck = new CompletableFuture<>();
      CompletableFuture<Void> previousParentStagedAck = parent.getAndSetStagedAck(newParentStagedAck);
      CompletableFuture<Void> merged = CompletableFuture.allOf(newParentStagedAck, child.getMessageAck());
      CompletableFutureUtils.propagate(merged, previousParentStagedAck);
    }
  }
}
