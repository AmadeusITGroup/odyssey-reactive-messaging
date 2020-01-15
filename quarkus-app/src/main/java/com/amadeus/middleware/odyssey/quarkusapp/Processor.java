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
import com.amadeus.middleware.odyssey.reactive.messaging.corporate.framework.EventContext;
import com.amadeus.middleware.odyssey.reactive.messaging.corporate.framework.EventContextMessageInitializer;
import com.amadeus.middleware.odyssey.reactive.messaging.kafka.connector.provider.KafkaRecordPublisher;

import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class Processor {
  private static final Logger logger = Logger.getLogger(Processor.class);

  @Inject
  KafkaRecordPublisher kafkaRecordPublisher; // just to not have it discarded by arc

  @Inject
  EventContextMessageInitializer eventContextMessageInitializer; // just to not have it discarded by arc

  @Inject
  private Topology topology;

  @Inject
  private ReactiveStreamFactory reactiveStreamFactory;

  @Inject
  MyMessageContext myMessageContext;

  @Inject
  Async<MyMessageContext> gmyMessageContextAsync;

  @Inject
  Message message;

  @Incoming("input_channel")
  @Outgoing("outgoing")
  @NodeName("processor1")
  public void processor1(Async<Message> async, MyMessageContext mmc, Message<String> msg, EventContext ec) {
    logger.infof("processor1 %s", msg);
    logger.infof("processor1 arc injected %s", message);
    logger.infof("processor1 async arc injected %s", async.get());
    logger.infof("processor1 mmc %s", mmc);
    logger.infof("processor1 EventContext %s", ec.toString());
  }

  @Incoming("outgoing")
  @Outgoing("terminal")
  public void processor2(Async<MyMessageContext> myMessageContextAsync, String msg) {
    logger.infof("processor2 %s", msg);
    logger.infof("processor2 myMessageContext %s", myMessageContext);
    logger.infof("processor2 myMessageContextAsync %s", myMessageContextAsync.get());
    logger.infof("processor2 gmyMessageContextAsync %s", gmyMessageContextAsync.get());
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
