package com.amadeus.middleware.odyssey.reactive.messaging.core.impl;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;

class MessageBuilder extends AbstractMessageBuilder {
  @Override
  public Message build() {
    MessageImpl message = new MessageImpl(messageContexts, payload);
    AbstractMessageBuilder.setupParentChildLink(parents, message);
    return message;
  }
}
