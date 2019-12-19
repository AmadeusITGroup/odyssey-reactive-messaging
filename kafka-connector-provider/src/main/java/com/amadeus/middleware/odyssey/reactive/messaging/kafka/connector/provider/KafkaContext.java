package com.amadeus.middleware.odyssey.reactive.messaging.kafka.connector.provider;

import java.util.List;

import org.apache.kafka.common.record.TimestampType;

import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageContext;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageScoped;

import io.vertx.reactivex.kafka.client.producer.KafkaHeader;

@MessageScoped
public interface KafkaContext extends MessageContext {
  String KEY = "MY_KAFKA_IMPLEMENTATION.KafkaContext";

  String topic();

  int partition();

  long offset();

  long timestamp();

  TimestampType timestampType();

  Object key();

  List<KafkaHeader> headers();

}
