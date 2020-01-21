package com.amadeus.middleware.odyssey.reactive.messaging.kafka.connector.provider;

import javax.inject.Inject;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageInitializer;

import io.vertx.reactivex.kafka.client.consumer.KafkaConsumerRecord;

public class KafkaMessageInitializer {

  @SuppressWarnings("rawtypes")
  @Inject
  Message message;

  @SuppressWarnings("unchecked")
  @MessageInitializer
  public void initialize() {

    // If the Metadata is already present, then do nothing.
    if (message.hasMergeableMetadata(KafkaIncoming.META_MERGE_KEY)) {
      return;
    }

    // If it is not a Kafka specific payload, do nothing.
    if (!KafkaConsumerRecord.class.isAssignableFrom(message.getPayload()
        .getClass())) {
      return;
    }

    // Create the KafkaIncoming and set the value with the KafkaPayload
    KafkaConsumerRecord<String, String> kcr = (KafkaConsumerRecord<String, String>) message.getPayload();
    KafkaIncomingImpl ki = new KafkaIncomingImpl(kcr);
    message.addMetadata(ki);
    message.setPayload(kcr.value());
  }
}
