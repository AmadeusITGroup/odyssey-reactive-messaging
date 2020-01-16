package com.amadeus.middleware.odyssey.reactive.messaging.core.cdiextension;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Metadata;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageScoped;

@MessageScoped
public interface ParameterizedContext<T> extends Metadata {
  String KEY = "PARAMETERIZED_CONTEXT";
  String MERGE_KEY = KEY;
  String sayHello();
}
