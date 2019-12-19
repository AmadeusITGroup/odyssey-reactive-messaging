package com.amadeus.middleware.odyssey.reactive.messaging.core.cdiextension;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.function.Function;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.BeanManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageContext;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageScoped;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.MessageContextFactory;

public class MessageContextProducer implements Function<CreationalContext<MessageContext>, MessageContext> {
  private static final Logger logger = LoggerFactory.getLogger(MessageContextProducer.class);

  private BeanManager beanManager;

  private Class<? extends MessageContext> clazz;

  private MessageContextFactory messageContextFactory;

  public MessageContextProducer(BeanManager beanManager, Class<? extends MessageContext> clazz,
      MessageContextFactory messageContextFactory) {
    Objects.requireNonNull(beanManager);
    Objects.requireNonNull(clazz);
    Objects.requireNonNull(messageContextFactory);
    this.beanManager = beanManager;
    this.clazz = clazz;
    this.messageContextFactory = messageContextFactory;
  }

  @Override
  public MessageContext apply(CreationalContext<MessageContext> cc) {
    // If the MessageContext is in the message associated to the (sub-) scope,
    // let's use it:
    MessageScopedContext context = (MessageScopedContext) beanManager.getContext(MessageScoped.class);
    Message<?> message = context.getMessage();
    if (message != null) {
      for (MessageContext mc : message.getContexts()) {
        if (clazz.isAssignableFrom(mc.getClass())) {
          return mc;
        }
      }
    } else {
      logger.error("Requesting a MessageContext={} with no Message in scope", cc.toString());
    }
    // Else, we call the factory:
    try {
      return messageContextFactory.create(clazz);
    } catch (InvocationTargetException | IllegalAccessException e) {
      logger.error("Failed to create a MessageContext of type {}, expection={}", clazz, e);
    }
    return null;
  }
}
