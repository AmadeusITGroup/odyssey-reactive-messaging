package com.amadeus.middleware.odyssey.reactive.messaging.core.cdiextension;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Metadata;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageScoped;

@MessageScoped
public interface BasicContext extends Metadata {
  String KEY = "BASIC_CONTEXT";
  String MERGE_KEY = KEY;
  String sayHello();
}
