package com.amadeus.middleware.odyssey.reactive.messaging.core.cdiextension;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageScoped;

@ApplicationScoped
public class MessageProducer {
  private static final Logger logger = LoggerFactory.getLogger(MessageProducer.class);

  @SuppressWarnings("rawtypes")
  @Produces
  @MessageScoped
  public static Message produceMessage(BeanManager beanManager) {
    MessageScopedContext msc = (MessageScopedContext) beanManager.getContext(MessageScoped.class);
    Message message = msc.getMessage();
    if (message == null) {
      logger.error("Null message from CDI context");
    }
    return msc.getMessage();
  }

}
