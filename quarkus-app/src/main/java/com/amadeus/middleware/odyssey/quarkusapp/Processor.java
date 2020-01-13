package com.amadeus.middleware.odyssey.quarkusapp;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.jboss.logging.Logger;
import org.reactivestreams.Publisher;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Async;
import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;
import com.amadeus.middleware.odyssey.reactive.messaging.core.NodeName;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.FunctionInvocationException;
import com.amadeus.middleware.odyssey.reactive.messaging.core.reactive.ReactiveStreamFactory;
import com.amadeus.middleware.odyssey.reactive.messaging.core.topology.Topology;

import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class Processor {
  private static final Logger logger = Logger.getLogger(Processor.class);

  @Inject
  private Topology topology;

  @Inject
  private ReactiveStreamFactory reactiveStreamFactory;

  @Inject
  MyMessageContext myMessageContext;

  @Inject
  Message message;

  @Outgoing("incoming")
  public Publisher publisher() {
    Message msg = Message.builder()
        .addContext(new MyMessageContextImpl("Hello world!"))
        .payload(new String("hello"))
        .build();
    return ReactiveStreams.of(msg)
        .buildRs();
  }

  @Incoming("incoming")
  @Outgoing("outgoing")
  @NodeName("processor1")
  public void processor1(Async<Message> async, MyMessageContext mmc, Message<String> msg) {
    logger.infof("processor1 %s", msg);
    logger.infof("processor1 di injected %s", message);
    logger.infof("processor1 mmc %s", mmc);
  }

  @Incoming("outgoing")
  @Outgoing("terminal")
  public void processor2(String msg) {
    logger.infof("processor2 %s", msg);
    logger.infof("myMessageContext %s", myMessageContext.toString());
  }

  @Incoming("terminal")
  public void terminalProcessor(Async<Message> async, Message<String> msg) {
    logger.infof("outGoingProcessor %s", msg);
  }

  void onStart(@Observes StartupEvent ev) throws FunctionInvocationException {
    reactiveStreamFactory.build(topology);
  }
}
