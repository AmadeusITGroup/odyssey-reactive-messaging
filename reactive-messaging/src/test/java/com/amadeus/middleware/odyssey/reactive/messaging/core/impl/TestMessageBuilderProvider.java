package com.amadeus.middleware.odyssey.reactive.messaging.core.impl;

import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageBuilder;

public class TestMessageBuilderProvider implements MessageBuilderProvider {
  @Override
  public MessageBuilder build() {
    return new TestMessageBuilder();
  }
}
