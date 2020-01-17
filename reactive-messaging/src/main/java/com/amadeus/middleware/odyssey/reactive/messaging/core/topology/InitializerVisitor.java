package com.amadeus.middleware.odyssey.reactive.messaging.core.topology;

public class InitializerVisitor extends AbstractVisitor {
  @Override
  public void visit(Node node) {
    if (node instanceof ProcessorNode) {
      ProcessorNode processorNode = (ProcessorNode) node;
      processorNode.getFunctionInvoker().initialize();
    } else if( node instanceof PublisherNode) {
      PublisherNode publisherNode = (PublisherNode) node;
      publisherNode.getPublisherInvoker().initialize();
    }
  }
}
