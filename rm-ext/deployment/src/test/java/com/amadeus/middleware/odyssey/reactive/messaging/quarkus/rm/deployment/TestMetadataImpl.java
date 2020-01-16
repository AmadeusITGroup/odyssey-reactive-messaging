package com.amadeus.middleware.odyssey.reactive.messaging.quarkus.rm.deployment;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Metadata;

public class TestMetadataImpl implements TestMetadata {
  private String text;

  TestMetadataImpl(String text) {
    this.text = text;
  }

  @Override
  public String getText() {
    return text;
  }

  @Override
  public String getMetadataKey() {
    return "X";
  }

  @Override
  public boolean isMetadataPropagable() {
    return false;
  }

  @Override
  public String getMetadataMergeKey() {
    return null;
  }

  @Override
  public Metadata metadataMerge(Metadata... metadata) {
    return null;
  }

  @Override
  public String toString() {
    return "TestMetadataImpl{" +
      "text='" + text + '\'' +
      '}';
  }
}
