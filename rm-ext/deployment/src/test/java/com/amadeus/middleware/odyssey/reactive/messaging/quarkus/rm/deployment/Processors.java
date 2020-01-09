package com.amadeus.middleware.odyssey.reactive.messaging.quarkus.rm.deployment;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.jboss.logging.Logger;
import org.reactivestreams.Publisher;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Async;
import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;
import com.amadeus.middleware.odyssey.reactive.messaging.core.NodeName;
import com.amadeus.middleware.odyssey.reactive.messaging.core.reactive.ReactiveStreamFactory;
import com.amadeus.middleware.odyssey.reactive.messaging.core.topology.Topology;

@ApplicationScoped
public class Processors {
  private static final Logger logger = Logger.getLogger(Processors.class);

  @Inject
  private Topology topology;

  @Inject
  private Message<String> message;

  @Inject
  private Message message2;

  @Inject
  TestMessageContext testMessageContext;

  @Inject
  public Processors(Message<String> message) {
  }

  @Inject
  public void initialize(Message<Integer> message) {
  }

  public Message getMessage() {
    return message;
  }

  public ReactiveStreamFactory getReactiveStreamFactory() {
    return reactiveStreamFactory;
  }

  @Inject
  ReactiveStreamFactory reactiveStreamFactory;

  public Topology getTopology() {
    return topology;
  }

  @Outgoing("incoming")
  public Publisher publisher() {
    Message message = Message.builder()
        .addContext(new TestMessageContextImpl("Test0"))
        .payload(new String("Hello"))
        .build();
    return ReactiveStreams.of(message)
        .buildRs();
  }

  @Incoming("incoming")
  @Outgoing("outgoing")
  @NodeName("processor1")
  public void processor1(Async<Message> async, Message<String> msg) {
    logger.infof("processor1 %s", msg);
    logger.infof("processor1 %s", message);
  }

  @Incoming("outgoing")
  @Outgoing("terminal")
  public void processor2(String msg) {
    logger.infof("processor2 %s", msg);
    logger.infof("testMessageContext %s", testMessageContext);
  }

  @Incoming("terminal")
  public void terminalProcessor(Async<Message> async, Message<String> msg) {
    logger.infof("outGoingProcessor %s", msg);
  }

}
