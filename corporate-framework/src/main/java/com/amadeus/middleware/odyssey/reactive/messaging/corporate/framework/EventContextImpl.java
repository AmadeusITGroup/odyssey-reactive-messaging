package com.amadeus.middleware.odyssey.reactive.messaging.corporate.framework;

import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageContext;

public class EventContextImpl implements EventContext {
  private String uniqueMessageId;

  private String eventKey;

  @Override
  public String getUniqueMessageId() {
    return uniqueMessageId;
  }

  @Override
  public void setUniqueMessageId(String uniqueMessageId) {
    this.uniqueMessageId = uniqueMessageId;
  }

  @Override
  public String getEventKey() {
    return eventKey;
  }

  @Override
  public void setEventKey(String key) {
    this.eventKey = key;
  }

  @Override
  public String toString() {
    return "EventContext{" + "uniqueMessageId='" + uniqueMessageId + '\'' + ", key='" + eventKey + '\'' + '}';
  }

  @Override
  public MessageContext merge(MessageContext... messageContexts) {
    throw new RuntimeException("Not implemented");
  }

  @Override
  public boolean isPropagable() {
    return true;
  }

  @Override
  public String getContextKey() {
    return KEY;
  }

  @Override
  public String getContextMergeKey() {
    return MERGE_KEY;
  }
}
