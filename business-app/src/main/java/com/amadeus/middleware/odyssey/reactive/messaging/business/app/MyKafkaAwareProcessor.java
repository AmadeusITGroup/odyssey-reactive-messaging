package com.amadeus.middleware.odyssey.reactive.messaging.business.app;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import com.amadeus.middleware.odyssey.reactive.messaging.core.NodeName;
import com.amadeus.middleware.odyssey.reactive.messaging.kafka.connector.provider.KafkaIncoming;
import com.amadeus.middleware.odyssey.reactive.messaging.kafka.connector.provider.KafkaTarget;

@ApplicationScoped
public class MyKafkaAwareProcessor {

  @Incoming("kafka_channel")
  @Outgoing("rxin")
  @NodeName("stage5")
  public void stage5(KafkaIncoming kafkaIncoming, KafkaTarget kafkaTarget) {
    kafkaTarget.topic("target_topic");
    kafkaTarget.key(kafkaIncoming.key());
  }
}
