package com.amadeus.middleware.odyssey.reactive.messaging.corporate.framework;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Metadata;

public class EventMetadataImpl implements EventMetadata {
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
    return "EventMetadata{" + "uniqueMessageId='" + uniqueMessageId + '\'' + ", key='" + eventKey + '\'' + '}';
  }

  @Override
  public Metadata metadataMerge(Metadata... metadata) {
    throw new RuntimeException("Not implemented");
  }

  @Override
  public boolean isMetadataPropagable() {
    return true;
  }

  @Override
  public String getMetadataKey() {
    return KEY;
  }

  @Override
  public String getMetadataMergeKey() {
    return MERGE_KEY;
  }
}
