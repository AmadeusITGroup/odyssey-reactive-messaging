package com.amadeus.middleware.odyssey.quarkusapp;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Metadata;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageScoped;

@MessageScoped
public interface MyMetadata extends Metadata {
  String KEY = "MY_MESSAGE_CONTEXT";

  String getText();
}
