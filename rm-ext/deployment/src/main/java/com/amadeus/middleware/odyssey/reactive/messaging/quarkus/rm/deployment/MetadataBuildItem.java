package com.amadeus.middleware.odyssey.reactive.messaging.quarkus.rm.deployment;

import io.quarkus.builder.item.MultiBuildItem;

public final class MetadataBuildItem extends MultiBuildItem {

  private final Class<?> clazz;

  public MetadataBuildItem(Class<?> clazz) {
    this.clazz = clazz;
  }

  public Class<?> getClazz() {
    return clazz;
  }
}
