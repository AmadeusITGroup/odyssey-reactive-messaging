package com.amadeus.middleware.odyssey.quarkusapp;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Metadata;

public class MyMetadataImpl implements MyMetadata {
  private String text;

  public MyMetadataImpl(String text) {
    this.text = text;
  }

  @Override
  public String getText() {
    return text;
  }

  @Override
  public String getMetadataKey() {
    return KEY;
  }

  @Override
  public boolean isMetadataPropagable() {
    return false;
  }

  @Override
  public String getMetadataMergeKey() {
    return KEY;
  }

  @Override
  public Metadata metadataMerge(Metadata... metadata) {
    return null;
  }

  @Override
  public String toString() {
    return "MyMetadataImpl{" + "text='" + text + '\'' + '}';
  }
}
