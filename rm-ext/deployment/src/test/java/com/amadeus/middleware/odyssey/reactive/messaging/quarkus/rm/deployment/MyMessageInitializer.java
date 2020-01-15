package com.amadeus.middleware.odyssey.reactive.messaging.quarkus.rm.deployment;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.logging.Logger;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageInitializer;

@ApplicationScoped
public class MyMessageInitializer {
  private static final Logger logger = Logger.getLogger(MyMessageInitializer.class);

  @MessageInitializer
  public Object callMe(Message message) {
    logger.info("called!");
    return null;
  }
}
