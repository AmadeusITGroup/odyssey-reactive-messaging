package com.amadeus.middleware.odyssey.reactive.messaging.core;

public interface MutableMessageContext extends MessageContext {

  MutableMessageContext createChild();
}
