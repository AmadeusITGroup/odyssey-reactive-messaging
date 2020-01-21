package com.amadeus.middleware.odyssey.reactive.messaging.business.app;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;
import com.amadeus.middleware.odyssey.reactive.messaging.core.NodeName;
import com.amadeus.middleware.odyssey.reactive.messaging.corporate.framework.EventMetadata;
import com.amadeus.middleware.odyssey.reactive.messaging.kafka.connector.provider.KafkaTarget;

@ApplicationScoped
public class MyOutgoingProcessor {
  private static final Logger logger = LoggerFactory.getLogger(MyOutgoingProcessor.class);

  @Inject
  private Message<String> message;

  @Inject
  private EventMetadata ec;

  @Inject
  public MyOutgoingProcessor(Message<String> message) {
    this.message = message;
  }

  @Incoming("output_channel")
  @NodeName("output")
  public void output() {
    logger.info("output: acking the message");

    // Look for a possible KafkaTarget
    KafkaTarget kafkaTarget = message.getMetadata(KafkaTarget.META_KEY);
    if (kafkaTarget != null) {
      logger.debug("If I where a Kafka connector I would send msgId={} to topic={} with key={}",
          ec.getUniqueMessageId(), kafkaTarget.topic(), kafkaTarget.key());
    }

    message.getStagedAck()
        .complete(null);
  }
}
