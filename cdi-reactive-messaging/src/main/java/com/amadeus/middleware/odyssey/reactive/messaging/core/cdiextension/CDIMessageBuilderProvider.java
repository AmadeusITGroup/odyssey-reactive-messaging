package com.amadeus.middleware.odyssey.reactive.messaging.core.cdiextension;

import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageBuilder;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.MessageBuilderProvider;

public class CDIMessageBuilderProvider implements MessageBuilderProvider {

  public MessageBuilder build() {
    return new CDIMessageBuilderImpl();
  }
}
