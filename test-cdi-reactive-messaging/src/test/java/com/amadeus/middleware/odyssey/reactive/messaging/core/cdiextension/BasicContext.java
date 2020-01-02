package com.amadeus.middleware.odyssey.reactive.messaging.core.cdiextension;

import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageContext;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageScoped;

@MessageScoped
public interface BasicContext extends MessageContext {
  String KEY = "BASIC_CONTEXT";
  String MERGE_KEY = KEY;
  String sayHello();
}
