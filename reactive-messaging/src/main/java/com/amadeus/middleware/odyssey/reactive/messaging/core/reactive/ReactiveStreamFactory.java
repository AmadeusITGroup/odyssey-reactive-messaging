package com.amadeus.middleware.odyssey.reactive.messaging.core.reactive;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.FunctionInvocationException;
import com.amadeus.middleware.odyssey.reactive.messaging.core.topology.PublisherNode;
import com.amadeus.middleware.odyssey.reactive.messaging.core.topology.Topology;

@ApplicationScoped
public class ReactiveStreamFactory {
  private static final Logger logger = LoggerFactory.getLogger(ReactiveStreamFactory.class);

  @Inject
  private BeanManager beanManager;

  // Simplistic limited build logic
  public void build(Topology topology) throws FunctionInvocationException {
    Instance<Object> instance = beanManager.createInstance();
    for (PublisherNode<?> node : topology.getPublisherNodes()) {
      node.accept(new ReactiveStreamBuilderVisitor(instance));
    }
  }
}
