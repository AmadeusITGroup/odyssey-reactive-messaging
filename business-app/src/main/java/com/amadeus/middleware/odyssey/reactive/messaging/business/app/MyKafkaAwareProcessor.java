package com.amadeus.middleware.odyssey.reactive.messaging.business.app;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amadeus.middleware.odyssey.reactive.messaging.core.NodeName;
import com.amadeus.middleware.odyssey.reactive.messaging.kafka.connector.provider.KafkaContext;
import com.amadeus.middleware.odyssey.reactive.messaging.kafka.connector.provider.KafkaTarget;

@ApplicationScoped
public class MyKafkaAwareProcessor {
  private static final Logger logger = LoggerFactory.getLogger(MyKafkaAwareProcessor.class);

  @Incoming("kafka_channel")
  @Outgoing("rxin")
  @NodeName("stage5")
  public void stage5(KafkaContext kafkaContext, KafkaTarget kafkaTarget) {
    kafkaTarget.topic("target_topic");
    kafkaTarget.key(kafkaContext.key());
  }
}
