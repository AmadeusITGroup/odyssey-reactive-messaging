package com.amadeus.middleware.odyssey.reactive.messaging.core.cdiextension;

import java.util.concurrent.atomic.AtomicLong;

import javax.enterprise.inject.spi.CDI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageContext;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.AbstractMessageBuilder;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.MessageImpl;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.ReactiveMessagingContext;

public class CDIMessageBuilderImpl<T> extends AbstractMessageBuilder<T> {
  private static final Logger logger = LoggerFactory.getLogger(CDIMessageBuilderImpl.class);

  private static AtomicLong messageScopeId = new AtomicLong();

  @Override
  public Message<T> build() {
    MessageImpl<T> message = new MessageImpl<>(messageContexts, payload);

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

  private void buildCdiContext(MessageImpl message) {
    // Get a new unique MessageScope identifier
    String msi = Long.toString(messageScopeId.getAndIncrement());
    logger.debug("new message with scopeid={}", msi);
    message.setScopeContextId(msi);

    // Build and populate the (sub-) context
    MessageScopedContext context = MessageScopedContext.getInstance();
    boolean alreadyActive = context.isActive(); // if the context is already active don't reactivate/suspend it
    if (!alreadyActive) {
      context.start(msi);
    }
    try {
      // Attach the Message pojo to the CDI context so it can be used to back the Message cdi bean
      context.setMessage(message);

      // Let's request to the CDI container the Message bean
      CDI.current()
          .select(Message.class)
          .get()
          .getPayload(); // method call to force instance creation

      // Let's request to the CDI container for the MessageContext instanciation
      if (ReactiveMessagingContext.getMessageContextFactory().getMessageContext() != null) {
        for (Class<MessageContext> mcc : ReactiveMessagingContext.getMessageContextFactory().getMessageContext()) {
          // some method calls seems to be intercepted by the CDI proxy, e.g.: getClass()
          // thus calling getIdentifyingKey() to go to the final instance and for bean instanciation by the container
          CDI.current()
              .select(mcc)
              .get()
              .getIdentifyingKey();
        }
      }
    } finally {
      context.setMessage(null);
      if (!alreadyActive) {
        context.suspend();
      }
    }
  }
}
