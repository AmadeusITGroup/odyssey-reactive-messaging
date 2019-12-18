package com.amadeus.middleware.odyssey.reactive.messaging.core.cdiextension;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageContext;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageContextProducerException;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.AbstractMessageBuilder;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.MessageImpl;
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
      context.add(msi, Message.class, message);

      message.getContexts()
          .forEach(msgCtx -> {
            // TODO: make the actual implementation
            // Register the messageContext using its interface annotated with @MessageScoped
            // For now: assume it is its single first-interface..
            Class<?> clazz = msgCtx.getClass();
            clazz = clazz.getInterfaces()[0];
            context.add(msi, clazz, msgCtx);
          });

      // Let's request to the CDI container for the MessageContext instanciation
      if (ReactiveMessagingContext.getMessageContextFactory()
          .getMessageContext() != null) {
        mccloop: for (Class<? extends MessageContext> mcc : ReactiveMessagingContext.getMessageContextFactory()
            .getMessageContext()) {

          Iterable<MessageContext> mcs = message.getContexts();
          for (MessageContext mc : mcs) {
            if (mcc.isAssignableFrom(mc.getClass())) {
              continue mccloop;
            }
          }
          try {
            MessageContext nmc = ReactiveMessagingContext.getMessageContextFactory()
                .create(mcc);
            message.addContext(nmc);
            context.add(msi, mcc, nmc);
          } catch (InvocationTargetException | IllegalAccessException e) {
            throw new MessageContextProducerException(e);
          }
        }
      }
    } finally {
      if (!alreadyActive) {
        context.suspend();
      }
    }
  }
}
