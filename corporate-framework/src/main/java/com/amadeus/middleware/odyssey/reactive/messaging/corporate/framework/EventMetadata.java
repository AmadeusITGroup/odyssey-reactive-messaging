package com.amadeus.middleware.odyssey.reactive.messaging.corporate.framework;

import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageScoped;
import com.amadeus.middleware.odyssey.reactive.messaging.core.Metadata;

@MessageScoped
public interface EventMetadata extends Metadata {
  String KEY = "MY_EVENT_METADATA";
  String MERGE_KEY = KEY;

  String getUniqueMessageId();

  void setUniqueMessageId(String uniqueMessageId);

  String getEventKey();

  void setEventKey(String key);
}
