package com.amadeus.middleware.odyssey.quarkusapp;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.jboss.logging.Logger;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Async;
import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;
import com.amadeus.middleware.odyssey.reactive.messaging.core.NodeName;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.FunctionInvocationException;
import com.amadeus.middleware.odyssey.reactive.messaging.core.reactive.ReactiveStreamFactory;
import com.amadeus.middleware.odyssey.reactive.messaging.core.topology.Topology;
import com.amadeus.middleware.odyssey.reactive.messaging.corporate.framework.EventMetadata;
import com.amadeus.middleware.odyssey.reactive.messaging.corporate.framework.EventMetadataMessageInitializer;
import com.amadeus.middleware.odyssey.reactive.messaging.kafka.connector.provider.KafkaRecordPublisher;

import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class Processor {
  private static final Logger logger = Logger.getLogger(Processor.class);

  @Inject
  KafkaRecordPublisher kafkaRecordPublisher; // just to not have it discarded by arc

  @Inject
  EventMetadataMessageInitializer eventMetadataMessageInitializer; // just to not have it discarded by arc

  @Inject
  private Topology topology;

  @Inject
  private ReactiveStreamFactory reactiveStreamFactory;

  @Inject
  MyMetadata myMetadata;

  @Inject
  Async<MyMetadata> gmyMetadataAsync;

  @Inject
  Message message;

  @Incoming("input_channel")
  @Outgoing("outgoing")
  @NodeName("processor1")
  public void processor1(Async<Message> async, MyMetadata mmc, Message<String> msg, EventMetadata ec) {
    logger.infof("processor1 %s", msg);
    logger.infof("processor1 arc injected %s", message);
    logger.infof("processor1 async arc injected %s", async.get());
    logger.infof("processor1 mmc %s", mmc);
    logger.infof("processor1 EventContext %s", ec.toString());
  }

  @Incoming("outgoing")
  @Outgoing("terminal")
  public void processor2(Async<MyMetadata> myMetadataAsync, String msg) {
    logger.infof("processor2 %s", msg);
    logger.infof("processor2 myMetadata %s", myMetadata);
    logger.infof("processor2 myMetadataAsync %s", myMetadataAsync.get());
    logger.infof("processor2 gmyMetadataAsync %s", gmyMetadataAsync.get());
  }

  @Incoming("terminal")
  public void terminalProcessor(Async<Message> async, Message<String> msg) {
    logger.infof("outGoingProcessor %s", msg);
    logger.infof("outGoingProcessor async=%s", async.get());
  }

  void onStart(@Observes StartupEvent ev, BeanManager beanManager) throws FunctionInvocationException {
    reactiveStreamFactory.build(topology);
    KafkaRecordPublisher kafkaRecordPublisher = beanManager.createInstance()
        .select(KafkaRecordPublisher.class)
        .get();
  }
}
