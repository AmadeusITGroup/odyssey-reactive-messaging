package com.amadeus.middleware.odyssey.reactive.messaging.core.reactive;

import javax.enterprise.inject.Instance;

import org.eclipse.microprofile.reactive.streams.operators.CompletionRunner;
import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.FunctionInvocationException;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.FunctionInvoker;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.PublisherInvoker;
import com.amadeus.middleware.odyssey.reactive.messaging.core.topology.AbstractVisitor;
import com.amadeus.middleware.odyssey.reactive.messaging.core.topology.Node;
import com.amadeus.middleware.odyssey.reactive.messaging.core.topology.ProcessorNode;
import com.amadeus.middleware.odyssey.reactive.messaging.core.topology.PublisherNode;
import com.amadeus.middleware.odyssey.reactive.messaging.core.topology.SubscriberNode;

/**
 * This will build a simple reactive stream provide that the topology is a simple line.
 */
class ReactiveStreamBuilderVisitor extends AbstractVisitor {
  private static final Logger logger = LoggerFactory.getLogger(ReactiveStreamBuilderVisitor.class);

  private PublisherBuilder<?> publisherBuilder;
  private CompletionRunner<Void> completionRunner;
  private Instance<Object> instance;

  public ReactiveStreamBuilderVisitor(Instance<Object> instance) {
    this.instance = instance;
  }

  @Override
  public void visit(Node node) {
    if (node instanceof PublisherNode) {
      build((PublisherNode) node);
    } else if (node instanceof ProcessorNode) {
      build((ProcessorNode) node);
    } else if (node instanceof SubscriberNode) {
      build((SubscriberNode) node);
    }
  }

  private void build(PublisherNode publisherNode) {
    PublisherInvoker<?> publisherInvoker = publisherNode.getPublisherInvoker();
    Object targetPublisherInstance = instance.select(publisherInvoker.getTargetClass())
        .get();
    try {
      Publisher<?> publisher = publisherInvoker.invoke(targetPublisherInstance);
      publisherBuilder = ReactiveStreams.fromPublisher(publisher);
    } catch (FunctionInvocationException e) {
      logger.error("Failure", e);
    }
  }

  private void build(ProcessorNode processorNode) {
    FunctionInvoker functionInvoker = processorNode.getFunctionInvoker();
    Object targetProcessorInstance = instance.select(functionInvoker.getTargetClass())
        .get();
    publisherBuilder = publisherBuilder.peek(m -> {
      try {
        functionInvoker.invoke(targetProcessorInstance, (Message<?>) m);
      } catch (FunctionInvocationException e) {
        logger.error("Failure", e);
      }
    });

    // if there is no child, make the subscription
    if (processorNode.getChildren()
        .isEmpty()) {
      completionRunner = publisherBuilder.forEach(m -> {
      });
      start();
    }
  }

  private void build(SubscriberNode subscriberNode) {
    completionRunner = publisherBuilder.to((Subscriber) subscriberNode.getSubscriber());
    start();
  }

  private void start() {
    completionRunner.run()
        .whenComplete((m, e) -> {
          if (e != null) {
            logger.error("Failed Stream", e);
          }
          logger.info("Stream end");
        });
  }
}
