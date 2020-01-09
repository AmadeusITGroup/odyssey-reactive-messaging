package com.amadeus.middleware.odyssey.reactive.messaging.quarkus.rm.deployment;

import org.jboss.jandex.Type;

import io.quarkus.builder.item.MultiBuildItem;

public final class MessageContextBuildItem extends MultiBuildItem {

  private final Class<?> clazz;

  public MessageContextBuildItem(Class<?> clazz) {
    this.clazz = clazz;
  }

  public Class<?> getClazz() {
    return clazz;
  }
}
