package com.amadeus.middleware.odyssey.reactive.messaging.corporate.framework;

import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageScoped;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageContext;

// Just for fun, let's have the implementation separated from the interface
@MessageScoped
public interface EventContext extends MessageContext {
  String getUniqueMessageId();

  void setUniqueMessageId(String uniqueMessageId);

  String getEventKey();

  void setEventKey(String key);
}
