package com.amadeus.middleware.odyssey.reactive.messaging.corporate.framework;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageInitializer;
import com.amadeus.middleware.odyssey.reactive.messaging.kafka.connector.provider.KafkaIncoming;

@ApplicationScoped
public class EventMetadataMessageInitializer {

  @Inject
  private KafkaIncoming kafkaIncoming;

  @Inject
  private Message<?> message;

  @MessageInitializer
  public void initialize(KafkaIncoming direcKafkaIncoming) {

    // If there is already a kind of EventContext, then do nothing.
    if (message.hasMergeableMetadata(EventMetadata.META_MERGE_KEY)) {
      return;
    }

    // Create the EventContext from the Kafka key
    EventMetadata ec = new EventMetadataImpl();
    ec.setEventKey((String) kafkaIncoming.key());
    message.addMetadata(ec);

    String payload = (String) message.getPayload();
    ec.setUniqueMessageId(payload.split("-")[0]);
  }
}
