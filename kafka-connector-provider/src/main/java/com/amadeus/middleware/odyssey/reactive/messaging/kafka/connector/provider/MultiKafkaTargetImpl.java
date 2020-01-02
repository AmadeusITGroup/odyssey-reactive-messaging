package com.amadeus.middleware.odyssey.reactive.messaging.kafka.connector.provider;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageContext;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MutableMessageContext;

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
  public MutableMessageContext createChild() {
    return new MultiKafkaTargetImpl(getTargets());
  }

  @Override
  public MessageContext merge(MessageContext... messageContexts) {
    for (MessageContext mc : messageContexts) {
      if (KafkaTarget.class.isAssignableFrom(mc.getClass())) {
        kafkaTargets.add((KafkaTarget) mc);
      } else if (MultiKafkaTarget.class.isAssignableFrom(mc.getClass())) {
        kafkaTargets.addAll(((MultiKafkaTarget) mc).getTargets());
      } else {
        logger.error("Cannot merge MessageContext={}", mc);
      }
    }
    return this;
  }

  @Override
  public boolean isPropagable() {
    return false;
  }

  @Override
  public String getContextKey() {
    return KEY;
  }

  @Override
  public String getContextMergeKey() {
    return MERGE_KEY;
  }
}
