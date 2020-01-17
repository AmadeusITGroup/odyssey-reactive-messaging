package com.amadeus.middleware.odyssey.reactive.messaging.core;

/**
 * This is a first try implementation:
 * A NodeInterceptor is called before/after a node invocation on an instrumented topology.
 */
public interface NodeInterceptor {

  void initialize(String nodeName);
}
