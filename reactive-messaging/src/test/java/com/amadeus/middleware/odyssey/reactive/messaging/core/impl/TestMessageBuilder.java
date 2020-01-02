package com.amadeus.middleware.odyssey.reactive.messaging.core.impl;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;

class TestMessageBuilder<T> extends AbstractMessageBuilder<T> {
  @Override
  public Message<T> build() {
    MessageImpl<T> message = new MessageImpl<>(contexts, payload);
    AbstractMessageBuilder.setupParentChildLink(parents, message);
    return message;
  }
}
