package com.amadeus.middleware.odyssey.reactive.messaging.core.cdiextension;

import javax.enterprise.inject.spi.BeanManager;

import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.PublisherInvokerImpl;
import com.amadeus.middleware.odyssey.reactive.messaging.core.topology.AbstractVisitor;
import com.amadeus.middleware.odyssey.reactive.messaging.core.topology.Node;
import com.amadeus.middleware.odyssey.reactive.messaging.core.topology.PublisherNode;

public class FunctionInvokerInitializer extends AbstractVisitor {
  private BeanManager beanManager;

  public FunctionInvokerInitializer(BeanManager beanManager) {
    this.beanManager = beanManager;
  }

  @Override
  public void visit(Node node) {
    if (node instanceof PublisherNode) {
      PublisherNode publisherNode = (PublisherNode) node;
      PublisherInvokerImpl<?> invoker = (PublisherInvokerImpl<?>) publisherNode.getPublisherInvoker();
      invoker.setTargetInstance(beanManager.createInstance()
          .select(invoker.getTargetClass())
          .get());
    }
  }
}
