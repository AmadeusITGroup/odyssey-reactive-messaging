package com.amadeus.middleware.odyssey.reactive.messaging.corporate.framework;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageContextBuilder;
import com.amadeus.middleware.odyssey.reactive.messaging.kafka.connector.provider.KafkaContext;

@ApplicationScoped
public class EventContextFactory {

  @Inject
  private KafkaContext kafkaContext;

  @Inject
  private Message message;

  @MessageContextBuilder
  public EventContext newMessageContext() {
    EventContext ec = new EventContextImpl();
    ec.setEventKey("0" /* (String) kafkaContext.key() */);
    String payload = (String) message.getPayload();
    ec.setUniqueMessageId(payload.split("-")[0]);
    return ec;
  }
}
