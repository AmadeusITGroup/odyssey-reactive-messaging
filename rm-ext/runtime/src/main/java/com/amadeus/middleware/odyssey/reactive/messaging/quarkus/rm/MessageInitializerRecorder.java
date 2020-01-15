package com.amadeus.middleware.odyssey.reactive.messaging.quarkus.rm;

import java.util.List;

import org.jboss.logging.Logger;

import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.ReactiveMessagingContext;

import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class MessageInitializerRecorder {

  private static final Logger logger = Logger.getLogger(MessageInitializerRecorder.class);

  public void initialize(List<QuarkusFunctionInvoker> quarkusFunctionInvokers) {
    logger.debugf("initialize: %s", quarkusFunctionInvokers);
    QuarkusMessageInitializerRegistry messageInitializerRegistry = new QuarkusMessageInitializerRegistry();
    for (QuarkusFunctionInvoker quarkusFunctionInvoker : quarkusFunctionInvokers) {
      quarkusFunctionInvoker.initialize();
      messageInitializerRegistry.add(quarkusFunctionInvoker);
    }
    ReactiveMessagingContext.setMessageInitializerRegistry(messageInitializerRegistry);
  }
}
