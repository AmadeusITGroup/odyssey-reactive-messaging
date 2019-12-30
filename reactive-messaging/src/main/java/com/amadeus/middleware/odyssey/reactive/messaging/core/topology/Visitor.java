package com.amadeus.middleware.odyssey.reactive.messaging.core.topology;

public interface Visitor {

  void beforeChildren();

  void afterChildren();

  void visit(Node node);
}
