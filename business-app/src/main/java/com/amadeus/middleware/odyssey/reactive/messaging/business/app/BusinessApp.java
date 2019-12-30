package com.amadeus.middleware.odyssey.reactive.messaging.business.app;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.FunctionInvocationException;
import com.amadeus.middleware.odyssey.reactive.messaging.core.reactive.ReactiveStreamFactory;
import com.amadeus.middleware.odyssey.reactive.messaging.core.topology.Topology;

public class BusinessApp {
  private static final Logger logger = LoggerFactory.getLogger(BusinessApp.class);

  public static void main(String... args) throws InterruptedException, FunctionInvocationException {
    try (SeContainer container = SeContainerInitializer.newInstance()
        .initialize()) {
      logger.info("container initialized");

      Instance<Object> instance = container.getBeanManager()
          .createInstance();
      Topology topology = instance.select(Topology.class)
          .get();
      ReactiveStreamFactory factory = instance.select(ReactiveStreamFactory.class)
          .get();
      factory.build(topology);

      Thread.sleep(600000);
    }
    logger.info("end");
  }
}
