package com.amadeus.middleware.odyssey.reactive.messaging.core.topology;

import java.util.ArrayList;
import java.util.List;

import org.reactivestreams.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amadeus.middleware.odyssey.reactive.messaging.core.FunctionInvoker;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.PublisherInvoker;

public class TopologyBuilder {
  private static final Logger logger = LoggerFactory.getLogger(TopologyBuilder.class);

  private List<ProcessorNode> processorNodes = new ArrayList<>();

  private List<PublisherNode<?>> publisherNodes = new ArrayList<>();

  private List<SubscriberNode<?>> subscriberNodes = new ArrayList<>();

  public List<ProcessorNode> getProcessorNodes() {
    return processorNodes;
  }

  public List<PublisherNode<?>> getPublisherNodes() {
    return publisherNodes;
  }

  public List<SubscriberNode<?>> getSubscriberNodes() {
    return subscriberNodes;
  }

  public ProcessorNode addProcessor(String name, FunctionInvoker functionInvoker, String[] inputChannels,
      String[] outputChannels) {
    ProcessorNode processorNode = new ProcessorNode(name, functionInvoker, inputChannels, outputChannels);
    processorNodes.add(processorNode);
    return processorNode;
  }

  public void addProcessorNode(ProcessorNode processorNode) {
    processorNodes.add(processorNode);
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public <T> PublisherNode addPublisherNode(String name, PublisherInvoker<?> publisherInvoker,
      String... outputChannels) {
    PublisherNode publisherNode = new PublisherNode(name, publisherInvoker, outputChannels);
    publisherNodes.add(publisherNode);
    return publisherNode;
  }

  public void addPublisherNode(PublisherNode<?> publisherNode) {
    publisherNodes.add(publisherNode);
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public <T> void addSubscriberNode(String name, Subscriber<T> subscriber, String... channelName) {
    subscriberNodes.add(new SubscriberNode(name, subscriber, channelName));
  }

  public void addSubscriberNode(SubscriberNode<?> subscriberNode) {
    subscriberNodes.add(subscriberNode);
  }

  public Topology build(Topology topology) {
    topology.addPublisherNodes(publisherNodes);
    topology.addProcessorNodes(processorNodes);
    topology.addSubscriberNodes(subscriberNodes);
    topology.initialize();
    return topology;
  }

  @Override
  public String toString() {
    return "TopologyBuilder{" + "processorNodes=" + processorNodes + ", publisherNodes=" + publisherNodes
        + ", subscriberNodes=" + subscriberNodes + '}';
  }
}
