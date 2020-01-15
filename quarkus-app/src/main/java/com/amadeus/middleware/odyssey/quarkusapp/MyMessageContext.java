package com.amadeus.middleware.odyssey.quarkusapp;

import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageContext;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageScoped;

@MessageScoped
public interface MyMessageContext extends MessageContext {
  String KEY = "MY_MESSAGE_CONTEXT";

  String getText();
}
