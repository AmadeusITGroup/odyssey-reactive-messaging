package com.amadeus.middleware.odyssey.reactive.messaging.quarkus.rm;

import org.jboss.logging.Logger;

import com.amadeus.middleware.odyssey.reactive.messaging.core.topology.Topology;
import com.amadeus.middleware.odyssey.reactive.messaging.core.topology.TopologyBuilder;

import io.quarkus.arc.runtime.BeanContainer;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class TopologyInitializerRecorder {
  private static final Logger logger = Logger.getLogger(TopologyInitializerRecorder.class);

  public void initialize(BeanContainer container, TopologyBuilder builder) {
    logger.debugf("initialize: %s", builder);
    Topology topology = container.instance(Topology.class);
    builder.build(topology);
    topology.accept(new FunctionInvokerInitializer());
  }
}
