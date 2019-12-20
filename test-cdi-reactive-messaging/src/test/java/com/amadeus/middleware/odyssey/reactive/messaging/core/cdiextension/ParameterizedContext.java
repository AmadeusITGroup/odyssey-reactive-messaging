package com.amadeus.middleware.odyssey.reactive.messaging.core.cdiextension;

import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageContext;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageScoped;

@MessageScoped
public interface ParameterizedContext<T> extends MessageContext {
  String KEY = "PARAMETERIZED_CONTEXT";
  String sayHello();
}
