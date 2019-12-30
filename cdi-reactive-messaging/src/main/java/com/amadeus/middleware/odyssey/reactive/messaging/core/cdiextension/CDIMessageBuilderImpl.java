package com.amadeus.middleware.odyssey.reactive.messaging.core.cdiextension;

import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageInitializerException;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.AbstractMessageBuilder;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.FunctionInvocationException;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.MessageImpl;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.MessageInitializerRegistry;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.ReactiveMessagingContext;

public class CDIMessageBuilderImpl<T> extends AbstractMessageBuilder<T> {
  private static final Logger logger = LoggerFactory.getLogger(CDIMessageBuilderImpl.class);

  private static AtomicLong messageScopeId = new AtomicLong();

  @Override
  public Message<T> build() {
    MessageImpl<T> message = new MessageImpl<T>(messageContexts, payload);

    AbstractMessageBuilder.setupParentChildLink(parents, message);

    if (dependencyInjection) {
      buildCdiContext(message);
      message.getMessageAck()
          .handle((c, e) -> {
            logger.debug("message acked scopeId={}", message.getScopeContextId());
            MessageScopedContext.getInstance()
                .destroy(message.getScopeContextId());
            return null;
          });
    }
    return message;
  }

  private void buildCdiContext(MessageImpl<T> message) {
    long msi = messageScopeId.getAndIncrement();
    message.setScopeContextId(msi);

    // Build and populate the (sub-) context
    MessageScopedContext context = MessageScopedContext.getInstance();
    boolean alreadyActive = context.isActive();
    if (!alreadyActive) {
      context.start(msi);
    }
    try {
      context.add(msi, message);
      callInitializers(message);
    } finally {
      if (!alreadyActive) {
        context.suspend();
      }
    }
  }

  private void callInitializers(Message<T> message) {
    MessageInitializerRegistry messageContextFactory = ReactiveMessagingContext.getMessageInitializerRegistry();
    try {
      messageContextFactory.initialize(message);
    } catch (FunctionInvocationException e) {
      throw new MessageInitializerException(e);
    }
  }
}
