package com.amadeus.middleware.odyssey.reactive.messaging.corporate.framework;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageInitializer;
import com.amadeus.middleware.odyssey.reactive.messaging.kafka.connector.provider.KafkaContext;

@ApplicationScoped
public class EventContextMessageInitializer {

  @Inject
  private KafkaContext kafkaContext;

  @Inject
  private Message<?> message;

  @MessageInitializer
  public void initialize(KafkaContext direcKafkaContext) {
    EventContext ec = new EventContextImpl();
    ec.setEventKey((String) kafkaContext.key());
    message.addContext(ec);

    String payload = (String) message.getPayload();
    ec.setUniqueMessageId(payload.split("-")[0]);
  }
}
