package com.amadeus.middleware.odyssey.reactive.messaging.quarkus.rm;

import com.amadeus.middleware.odyssey.reactive.messaging.core.topology.AbstractVisitor;
import com.amadeus.middleware.odyssey.reactive.messaging.core.topology.Node;
import com.amadeus.middleware.odyssey.reactive.messaging.core.topology.ProcessorNode;
import com.amadeus.middleware.odyssey.reactive.messaging.core.topology.PublisherNode;

public class FunctionInvokerInitializer extends AbstractVisitor {

  @Override
  public void visit(Node node) {
    if (node instanceof PublisherNode) {
      PublisherNode publisherNode = (PublisherNode) node;
      QuarkusPublisherInvoker quarkusPublisherInvoker = (QuarkusPublisherInvoker) publisherNode.getPublisherInvoker();
      quarkusPublisherInvoker.initialize();
    } else if (node instanceof ProcessorNode) {
      ProcessorNode processorNode = (ProcessorNode) node;
      QuarkusFunctionInvoker quarkusFunctionInvoker = (QuarkusFunctionInvoker) processorNode.getFunctionInvoker();
      quarkusFunctionInvoker.initialize();
    }
  }
}
