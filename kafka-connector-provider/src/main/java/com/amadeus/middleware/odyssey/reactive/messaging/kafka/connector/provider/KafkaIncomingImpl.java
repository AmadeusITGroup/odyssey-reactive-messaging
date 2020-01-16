package com.amadeus.middleware.odyssey.reactive.messaging.kafka.connector.provider;

import java.util.List;

import org.apache.kafka.common.record.TimestampType;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Metadata;

import io.vertx.reactivex.kafka.client.consumer.KafkaConsumerRecord;
import io.vertx.reactivex.kafka.client.producer.KafkaHeader;

public class KafkaIncomingImpl implements KafkaIncoming {

  private KafkaConsumerRecord<?, ?> kcr;

  KafkaIncomingImpl(KafkaConsumerRecord<?, ?> kcr) {
    this.kcr = kcr;
  }

  @Override
  public String topic() {
    return kcr.topic();
  }

  @Override
  public int partition() {
    return kcr.partition();
  }

  @Override
  public long offset() {
    return kcr.offset();
  }

  @Override
  public long timestamp() {
    return kcr.timestamp();
  }

  @Override
  public TimestampType timestampType() {
    return kcr.timestampType();
  }

  @Override
  public Object key() {
    return kcr.key();
  }

  @Override
  public List<KafkaHeader> headers() {
    return kcr.headers();
  }

  @Override
  public String toString() {
    return "{" + "topic='" + topic() + '\'' + ", partition='" + partition() + '\'' + ", offset='" + offset() + '\''
        + '}';
  }

  @Override
  public Metadata metadataMerge(Metadata... metadata) {
    throw new RuntimeException("Not implemented");
  }

  @Override
  public boolean isMetadataPropagable() {
    return false;
  }

  @Override
  public String getMetadataKey() {
    return KEY;
  }

  @Override
  public String getMetadataMergeKey() {
    return MERGE_KEY;
  }
}
