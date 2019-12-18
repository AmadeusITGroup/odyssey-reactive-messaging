package com.amadeus.middleware.odyssey.reactive.messaging.kafka.connector.provider;

import java.util.List;

import io.vertx.reactivex.kafka.client.producer.KafkaHeader;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageContext;
import org.apache.kafka.common.record.TimestampType;


import io.vertx.reactivex.kafka.client.consumer.KafkaConsumerRecord;

public class KafkaContextImpl implements KafkaContext {

  private KafkaConsumerRecord<?,?> kcr;

  KafkaContextImpl(KafkaConsumerRecord<?,?> kcr) {
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
  public MessageContext merge(MessageContext... messageContexts) {
    throw new RuntimeException("Not implemented");
  }

  @Override
  public String getIdentifyingKey() {
    return KEY;
  }
}
