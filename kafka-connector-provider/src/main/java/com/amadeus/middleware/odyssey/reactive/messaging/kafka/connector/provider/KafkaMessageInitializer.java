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

    // If the MessageContext is already present, then do nothing.
    // In this example, it will always be the case.
    if (message.getMessageContext(KafkaContext.KEY) != null) {
      return;
    }

    KafkaConsumerRecord<String, String> kcr = (KafkaConsumerRecord<String, String>) message.getPayload();
    KafkaContextImpl ki = new KafkaContextImpl(kcr);

    message.addContext(ki);
    message.setPayload(kcr.value());
  }
}
