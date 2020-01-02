package com.amadeus.middleware.odyssey.reactive.messaging.corporate.framework;

import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageContext;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageScoped;

@MessageScoped
public interface EventContext extends MessageContext {
  String KEY = "MY_EVENTCONTEXT";
  String MERGE_KEY = KEY;

  String getUniqueMessageId();

  void setUniqueMessageId(String uniqueMessageId);

  String getEventKey();

  void setEventKey(String key);
}
