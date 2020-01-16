package com.amadeus.middleware.odyssey.reactive.messaging.kafka.connector.provider;

import java.util.ArrayList;
import java.util.List;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Metadata;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MutableMetadata;

import io.vertx.reactivex.kafka.client.producer.KafkaHeader;

public class KafkaTargetImpl implements KafkaTarget {
  private String topic;
  private Object key;
  private List<KafkaHeader> headers;

  // Potential issue with the key here if it is a mutable reference (...)
  KafkaTargetImpl(String topic, Object key, List<KafkaHeader> headers) {
    this.topic = topic;
    this.key = key;
    this.headers = new ArrayList<>(headers);
  }

  @Override
  public String topic() {
    return topic;
  }

  @Override
  public void topic(String topic) {
    this.topic = topic;
  }

  @Override
  public Object key() {
    return key;
  }

  @Override
  public void key(Object key) {
    this.key = key;
  }

  /**
   * @return Direct reference to the internal header list.
   */
  @Override
  public List<KafkaHeader> headers() {
    return headers;
  }

  @Override
  public MutableMetadata createChild() {
    return new KafkaTargetImpl(this.topic, this.key, this.headers());
  }

  @Override
  public Metadata metadataMerge(Metadata... metadata) {
    throw new RuntimeException("Not implemented");
  }

  @Override
  public boolean isMetadataPropagable() {
    return true;
  }

  @Override
  public String getMetadataKey() {
    return KEY;
  }

  @Override
  public String getMetadataMergeKey() {
    return MERGE_KEY;
  }

  @Override
  public String toString() {
    return "KafkaTarget{" + "topic='" + topic + '\'' + ", key=" + key + ", headers=" + headers + '}';
  }
}
