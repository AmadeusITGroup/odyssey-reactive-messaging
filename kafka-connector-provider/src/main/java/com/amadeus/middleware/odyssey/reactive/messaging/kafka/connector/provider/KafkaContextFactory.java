package com.amadeus.middleware.odyssey.reactive.messaging.kafka.connector.provider;

import javax.inject.Inject;

import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageContextBuilder;
import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;

import io.vertx.reactivex.kafka.client.consumer.KafkaConsumerRecord;

public class KafkaContextFactory {

  @SuppressWarnings("rawtypes")
  @Inject
  Message message;

  @SuppressWarnings("unchecked")
  @MessageContextBuilder
  public KafkaContext create() {
    KafkaConsumerRecord<String, String> kcr = (KafkaConsumerRecord<String, String>) message.getPayload();
    KafkaContextImpl ki = new KafkaContextImpl(kcr);
    message.setPayload(kcr.value());
    return ki;
  }
}
