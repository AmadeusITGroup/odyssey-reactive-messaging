package com.amadeus.middleware.odyssey.reactive.messaging.core.impl;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;

class MessageBuilder<T> extends AbstractMessageBuilder<T> {
  @Override
  public Message<T> build() {
    MessageImpl<T> message = new MessageImpl<>(messageContexts, payload);
    AbstractMessageBuilder.setupParentChildLink(parents, message);
    return message;
  }
}
