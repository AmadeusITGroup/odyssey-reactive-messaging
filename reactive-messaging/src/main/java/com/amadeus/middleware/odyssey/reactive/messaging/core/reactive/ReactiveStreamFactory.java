package com.amadeus.middleware.odyssey.reactive.messaging.core.reactive;

import javax.enterprise.context.ApplicationScoped;

import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.FunctionInvocationException;
import com.amadeus.middleware.odyssey.reactive.messaging.core.topology.PublisherNode;
import com.amadeus.middleware.odyssey.reactive.messaging.core.topology.Topology;

@ApplicationScoped
public class ReactiveStreamFactory {

  // Simplistic limited build logic
  public void build(Topology topology) throws FunctionInvocationException {
    for (PublisherNode<?> node : topology.getPublisherNodes()) {
      node.accept(new ReactiveStreamBuilderVisitor());
    }
  }
}
