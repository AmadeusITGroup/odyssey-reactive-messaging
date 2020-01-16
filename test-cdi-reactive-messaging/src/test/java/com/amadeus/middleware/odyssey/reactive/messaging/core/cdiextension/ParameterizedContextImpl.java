package com.amadeus.middleware.odyssey.reactive.messaging.core.cdiextension;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Metadata;

public class ParameterizedContextImpl<T> implements ParameterizedContext<T> {
  @Override
  public String sayHello() {
    return "Hello";
  }

  @Override
  public Metadata metadataMerge(Metadata... metadata) {
    throw new RuntimeException("not implemented");
  }

  @Override
  public boolean isMetadataPropagable() {
    return false;
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
