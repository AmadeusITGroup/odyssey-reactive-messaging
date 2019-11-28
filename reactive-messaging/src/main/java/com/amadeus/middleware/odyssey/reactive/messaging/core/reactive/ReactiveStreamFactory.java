package com.amadeus.middleware.odyssey.reactive.messaging.core.reactive;

import java.util.Iterator;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amadeus.middleware.odyssey.reactive.messaging.core.topology.Node;
import com.amadeus.middleware.odyssey.reactive.messaging.core.topology.ProcessorNode;
import com.amadeus.middleware.odyssey.reactive.messaging.core.topology.PublisherNode;
import com.amadeus.middleware.odyssey.reactive.messaging.core.topology.SubscriberNode;
import com.amadeus.middleware.odyssey.reactive.messaging.core.topology.Topology;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.FunctionInvocationException;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.FunctionInvoker;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.MessageImpl;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.PublisherInvoker;

@ApplicationScoped
public class ReactiveStreamFactory {

  private static final Logger logger = LoggerFactory.getLogger(ReactiveStreamFactory.class);

  @Inject
  private BeanManager beanManager;

  // Simplistic limited build logic
  public void build(Topology topology) throws FunctionInvocationException {
    for (Node node : topology.getNodes()) {
      if (node instanceof PublisherNode) {
        processPublisher((PublisherNode) node);
      }
    }
  }

  private void processPublisher(PublisherNode publisherNode) throws FunctionInvocationException {
    Instance<Object> instance = beanManager.createInstance();

    PublisherInvoker publisherInvoker = publisherNode.getPublisherInvoker();
    Object targetPublisherInstance = instance.select(publisherInvoker.getTargetClass())
        .get();
    Publisher publisher = publisherInvoker.invoke(targetPublisherInstance);
    PublisherBuilder pb = ReactiveStreams.fromPublisher(publisher);

    Node node = publisherNode;
    while (true) {
      Iterator<Map.Entry<String, Node>> it = node.getChildren()
          .entrySet()
          .iterator();
      if (!it.hasNext()) {
        break;
      }
      Map.Entry<String, Node> child = it.next();
      node = child.getValue();
      if (ProcessorNode.class.isAssignableFrom(node.getClass())) {
        ProcessorNode processorNode = (ProcessorNode) node;
        FunctionInvoker functionInvoker = processorNode.getFunctionInvoker();
        Object targetProcessorInstance = instance.select(functionInvoker.getTargetClass())
            .get();
        pb = pb.peek(m -> {
          try {
            functionInvoker.invoke(targetProcessorInstance, (MessageImpl) m);
          } catch (FunctionInvocationException e) {
            logger.error("Failure", e);
          }
        });

        // if there is not child, make the subscription
        if (processorNode.getChildren()
            .isEmpty()) {
          pb.forEach(m -> {
          })
              .run()
              .whenComplete((m, e) -> {
                if (e != null) {
                  logger.error("Failed Stream", e);
                }
              });
          break;
        }
      } else if (SubscriberNode.class.isAssignableFrom(node.getClass())) {
        SubscriberNode subscriberNode = (SubscriberNode) node;
        pb.to(subscriberNode.getSubscriber())
            .run()
            .whenComplete((m, e) -> {
              if (e != null) {
                logger.error("Failed Stream", e);
              }
            });
      }
    }
  }
}
