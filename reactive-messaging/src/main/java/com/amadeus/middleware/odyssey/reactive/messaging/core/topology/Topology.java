package com.amadeus.middleware.odyssey.reactive.messaging.core.topology;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Topology {
  private List<Node> nodes;

  public void initialize(List<Node> nodes) {
    this.nodes = new ArrayList<>(nodes);
  }

  public List<Node> getNodes() {
    return new ArrayList<>(nodes);
  }
}
