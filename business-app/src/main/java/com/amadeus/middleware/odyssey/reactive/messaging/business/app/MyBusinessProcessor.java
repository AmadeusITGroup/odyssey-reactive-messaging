package com.amadeus.middleware.odyssey.reactive.messaging.business.app;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amadeus.middleware.odyssey.reactive.messaging.corporate.framework.EventContext;

public class MyBusinessProcessor {
  private static final Logger logger = LoggerFactory.getLogger(MyBusinessProcessor.class);

  @Incoming("input_channel")
  @Outgoing("business_internal_channel1")
  public void stage1(String payload) {
    logger.info("stage1 start");
    logger.info("stage1 payload={}", payload);
    logger.info("stage1 stop");
  }

  @Incoming("business_internal_channel1")
  @Outgoing("business_internal_channel2")
  public void stage2(Message<String> message) {
    logger.info("stage2 start");
    logger.info("stage2 payload={}", message.getPayload());
    logger.info("stage2 stop");
  }

  @Incoming("business_internal_channel2")
  @Outgoing("internal_channel")
  public void stage3(EventContext ec, String payload) {
    logger.info("stage3 start");
    logger.info("stage3 event id={} payload={}", ec.getUniqueMessageId(), payload);
    logger.info("stage3 stop");
  }
}
