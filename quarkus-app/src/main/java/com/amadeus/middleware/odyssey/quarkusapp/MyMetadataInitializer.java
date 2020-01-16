package com.amadeus.middleware.odyssey.quarkusapp;

import javax.enterprise.context.ApplicationScoped;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageInitializer;

@ApplicationScoped
@io.quarkus.arc.Unremovable
public class MyMetadataInitializer {

  @MessageInitializer
  public void justdoit(Message message) {
    message.addMetadata(new MyMetadataImpl("hello"));
  }
}
