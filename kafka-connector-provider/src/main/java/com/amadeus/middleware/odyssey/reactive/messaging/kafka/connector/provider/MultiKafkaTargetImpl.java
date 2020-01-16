package com.amadeus.middleware.odyssey.reactive.messaging.kafka.connector.provider;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Metadata;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MutableMetadata;

public class MultiKafkaTargetImpl implements MultiKafkaTarget {
  private static final Logger logger = LoggerFactory.getLogger(MultiKafkaTargetImpl.class);

  private List<KafkaTarget> kafkaTargets = new ArrayList<>();

  public MultiKafkaTargetImpl() {
  }

  private MultiKafkaTargetImpl(List<KafkaTarget> kafkaTargets) {
    this.kafkaTargets = kafkaTargets;
  }

  @Override
  public List<KafkaTarget> getTargets() {
    return new ArrayList<>(kafkaTargets);
  }

  @Override
  public MutableMetadata createChild() {
    return new MultiKafkaTargetImpl(getTargets());
  }

  @Override
  public Metadata metadataMerge(Metadata... metadata) {
    for (Metadata mc : metadata) {
      if (KafkaTarget.class.isAssignableFrom(mc.getClass())) {
        kafkaTargets.add((KafkaTarget) mc);
      } else if (MultiKafkaTarget.class.isAssignableFrom(mc.getClass())) {
        kafkaTargets.addAll(((MultiKafkaTarget) mc).getTargets());
      } else {
        logger.error("Cannot merge Metadata={}", mc);
      }
    }
    return this;
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
