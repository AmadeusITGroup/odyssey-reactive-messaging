package com.amadeus.middleware.odyssey.reactive.messaging.quarkus.rm.deployment;

import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageContext;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageScoped;

@MessageScoped
public interface TestMessageContext extends MessageContext {
  String getText();
}
