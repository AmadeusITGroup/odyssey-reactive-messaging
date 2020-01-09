package com.amadeus.middleware.odyssey.reactive.messaging.quarkus.rm.deployment;

import com.amadeus.middleware.odyssey.reactive.messaging.core.topology.TopologyBuilder;

import io.quarkus.builder.item.SimpleBuildItem;

public final class TopologyBuilderBuildItem extends SimpleBuildItem {

  private final TopologyBuilder builder;

  public TopologyBuilderBuildItem(TopologyBuilder builder) {
    this.builder = builder;
  }

  public TopologyBuilder getBuilder() {
    return builder;
  }
}
