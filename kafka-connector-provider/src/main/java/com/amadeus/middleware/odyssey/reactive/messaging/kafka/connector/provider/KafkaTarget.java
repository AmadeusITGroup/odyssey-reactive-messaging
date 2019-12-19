package com.amadeus.middleware.odyssey.reactive.messaging.kafka.connector.provider;

import java.util.List;

import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageScoped;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MutableMessageContext;

import io.vertx.reactivex.kafka.client.producer.KafkaHeader;

/**
 * Defines how Kafka will be targeted by the sink connector. The instance is initialized with the KafkaHeaders coming
 * from KafkaContext.
 */
@MessageScoped
public interface KafkaTarget extends MutableMessageContext {
  String KEY = "MY_KAFKA_IMPLEMENTATION.KafkaTarget";

  String topic();

  void topic(String topic);

  Object key();

  void key(Object key);

  List<KafkaHeader> headers();
}
