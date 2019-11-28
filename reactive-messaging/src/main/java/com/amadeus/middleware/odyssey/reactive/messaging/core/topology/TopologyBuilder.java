package com.amadeus.middleware.odyssey.reactive.messaging.core.topology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.reactivestreams.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.FunctionInvoker;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.PublisherInvoker;

public class TopologyBuilder {
  private static final Logger logger = LoggerFactory.getLogger(TopologyBuilder.class);

  private List<ProcessorNode> processorNodes = new ArrayList<>();

  private List<PublisherNode> publisherNodes = new ArrayList<>();

  private List<SubscriberNode> subscriberNodes = new ArrayList<>();

  public void addProcessor(String name, FunctionInvoker functionInvoker, String[] inputChannels,
      String[] outputChannels) {
    processorNodes.add(new ProcessorNode(name, functionInvoker, inputChannels, outputChannels));
  }

  public <T> void addPublisher(String name, PublisherInvoker publisherInvoker, String... outputChannels) {
    publisherNodes.add(new PublisherNode(name, publisherInvoker, outputChannels));
  }

  public <T> void addSubscriber(String name, Subscriber<T> subscriber, String... channelName) {
    subscriberNodes.add(new SubscriberNode(name, subscriber, channelName));
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

  public boolean isFullyConnected(List<Node> nodes) {
    // TODO
    return true;
  }

  public Topology build(Topology topology) {
    Map<String, ChannelBinding> channelToBinding = new HashMap<>();

    for (PublisherNode cp : publisherNodes) {
      cp.getChildren()
          .forEach((channel, node) -> channelToBinding.computeIfAbsent(channel, c -> new ChannelBinding())
              .addProducer(cp));
    }

    for (SubscriberNode cp : subscriberNodes) {
      cp.getParents()
          .forEach((channel, node) -> channelToBinding.computeIfAbsent(channel, c -> new ChannelBinding())
              .addConsumer(cp));
    }

    for (ProcessorNode processorNode : processorNodes) {
      processorNode.getParents()
          .forEach((channel, node) -> {
            channelToBinding.computeIfAbsent(channel, c -> new ChannelBinding())
                .addConsumer(processorNode);
          });
      processorNode.getChildren()
          .forEach((channel, node) -> {
            channelToBinding.computeIfAbsent(channel, c -> new ChannelBinding())
                .addProducer(processorNode);
          });
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

    List<Node> nodes = new ArrayList<>(publisherNodes.size() + subscriberNodes.size() + processorNodes.size());
    nodes.addAll(publisherNodes);
    nodes.addAll(subscriberNodes);
    nodes.addAll(processorNodes);

    // TODO: isFullConnected()

    topology.initialize(nodes);
    return topology;
  }
}
