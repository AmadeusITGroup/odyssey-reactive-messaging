package com.amadeus.middleware.odyssey.reactive.messaging.kafka.connector.provider;

import java.util.List;

import org.apache.kafka.common.record.TimestampType;

import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageScoped;
import com.amadeus.middleware.odyssey.reactive.messaging.core.Metadata;

import io.vertx.reactivex.kafka.client.producer.KafkaHeader;

@MessageScoped
public interface KafkaIncoming extends Metadata {
  String META_KEY = "MY_KAFKA_INCOMING";
  String META_MERGE_KEY = META_KEY;

  String topic();

  int partition();

  long offset();

  long timestamp();

  TimestampType timestampType();

  Object key();

  List<KafkaHeader> headers();

}
