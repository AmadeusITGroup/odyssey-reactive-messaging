package com.amadeus.middleware.odyssey.reactive.messaging.core.impl;

import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageBuilder;

public interface MessageBuilderProvider {
  @SuppressWarnings("rawtypes")
  MessageBuilder build();
}
