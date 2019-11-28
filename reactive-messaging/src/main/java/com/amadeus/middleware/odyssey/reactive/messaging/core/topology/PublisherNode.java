package com.amadeus.middleware.odyssey.reactive.messaging.core.topology;

import java.util.Arrays;

import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.PublisherInvoker;

public class PublisherNode<T> extends AbstractNode {
  private PublisherInvoker publisherInvoker;

  public PublisherNode(String name, PublisherInvoker publisherInvoker, String... outputChannels) {
    super(name);
    this.publisherInvoker = publisherInvoker;
    if (outputChannels != null) {
      Arrays.stream(outputChannels)
          .forEach(channelName -> this.children.put(channelName, null));
    }
  }

  public PublisherInvoker getPublisherInvoker() {
    return publisherInvoker;
  }
}
