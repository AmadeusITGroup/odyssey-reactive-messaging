package com.amadeus.middleware.odyssey.reactive.messaging.core.topology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class Topology {
  private static final Logger logger = LoggerFactory.getLogger(Topology.class);

  private List<PublisherNode<?>> publisherNodes = new ArrayList<>();
  private List<ProcessorNode> processorNodes = new ArrayList<>();
  private List<SubscriberNode<?>> subscriberNodes = new ArrayList<>();

  @SuppressWarnings("unchecked")
  void addPublisherNodes(Iterable<PublisherNode> publisherNodes) {
    for (PublisherNode publisherNode : publisherNodes) {
      this.publisherNodes.add(new PublisherNode(publisherNode));
    }
  }

  void addProcessorNodes(Iterable<ProcessorNode> processorNodes) {
    for (ProcessorNode processorNode : processorNodes) {
      this.processorNodes.add(new ProcessorNode(processorNode));
    }
  }

  @SuppressWarnings("unchecked")
  void addSubscriberNodes(Iterable<SubscriberNode> subscriberNodes) {
    for (SubscriberNode subscriberNode : subscriberNodes) {
      this.subscriberNodes.add(new SubscriberNode(subscriberNode));
    }
  }

  private static class ChannelBinding {
    public final List<Node> producers = new ArrayList<>();
    public final List<Node> consumers = new ArrayList<>();

    void addProducer(Node node) {
      producers.add(node);
    }

    void addConsumer(Node node) {
      consumers.add(node);
    }
  }

  void initialize() {
    Map<String, ChannelBinding> channelToBinding = new HashMap<>();

    for (PublisherNode<?> cp : publisherNodes) {
      cp.getChildren()
          .forEach((channel, node) -> channelToBinding.computeIfAbsent(channel, c -> new ChannelBinding())
              .addProducer(cp));
    }

    for (SubscriberNode<?> cp : subscriberNodes) {
      cp.getParents()
          .forEach((channel, node) -> channelToBinding.computeIfAbsent(channel, c -> new ChannelBinding())
              .addConsumer(cp));
    }

    for (ProcessorNode processorNode : processorNodes) {
      processorNode.getParents()
          .forEach((channel, node) -> channelToBinding.computeIfAbsent(channel, c -> new ChannelBinding())
              .addConsumer(processorNode));
      processorNode.getChildren()
          .forEach((channel, node) -> channelToBinding.computeIfAbsent(channel, c -> new ChannelBinding())
              .addProducer(processorNode));
    }

    for (Map.Entry<String, ChannelBinding> entry : channelToBinding.entrySet()) {
      String channelName = entry.getKey();
      ChannelBinding cb = entry.getValue();
      for (Node producer : cb.producers) {
        logger.trace("channel {} producer = {}", channelName, producer.getName());
      }
      for (Node consumer : cb.consumers) {
        logger.trace("channel {} consumer = {}", channelName, consumer.getName());
      }

      for (Node producer : cb.producers) {
        for (Node consumer : cb.consumers) {
          logger.trace("Edge from {} to {}", producer.getName(), consumer.getName());
          producer.addChildren(channelName, consumer);
          consumer.addParent(channelName, producer);
        }
      }
    }
  }

  public List<PublisherNode> getPublisherNodes() {
    return new ArrayList<>(publisherNodes);
  }

  public List<ProcessorNode> getProcessorNodes() {
    return new ArrayList<>(processorNodes);
  }

  public List<SubscriberNode> getSubscriberNodes() {
    return new ArrayList<>(subscriberNodes);
  }

  public void accept(Visitor visitor) {
    getPublisherNodes().forEach(pn -> pn.accept(visitor));
  }

  @Override
  public String toString() {
    return "Topology{" + "publisherNodes=" + publisherNodes + ", processorNodes=" + processorNodes
        + ", subscriberNodes=" + subscriberNodes + '}';
  }
}
