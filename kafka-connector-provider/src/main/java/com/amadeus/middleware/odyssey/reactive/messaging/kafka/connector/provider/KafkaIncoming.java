package com.amadeus.middleware.odyssey.reactive.messaging.kafka.connector.provider;

import java.util.List;

import org.apache.kafka.common.record.TimestampType;

import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageScoped;
import com.amadeus.middleware.odyssey.reactive.messaging.core.Metadata;

import io.vertx.reactivex.kafka.client.producer.KafkaHeader;

@MessageScoped
public interface KafkaIncoming extends Metadata {
  String KEY = "MY_KAFKA_INCOMING";
  String MERGE_KEY = KEY;

  String topic();

  int partition();

  long offset();

  long timestamp();

  TimestampType timestampType();

  Object key();

  List<KafkaHeader> headers();

}
