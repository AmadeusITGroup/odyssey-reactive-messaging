package com.amadeus.middleware.odyssey.reactive.messaging.business.app;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;

@ApplicationScoped
public class MyOutgoingProcessor {
  private static final Logger logger = LoggerFactory.getLogger(MyOutgoingProcessor.class);

  @Inject
  private Message message;

  @Incoming("output_channel")
  public void output() {
    logger.info("output: acking the message");
    message.getStagedAck()
        .complete(null);
  }
}
