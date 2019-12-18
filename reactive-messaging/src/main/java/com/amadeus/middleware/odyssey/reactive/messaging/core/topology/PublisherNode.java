package com.amadeus.middleware.odyssey.reactive.messaging.core.topology;

import java.util.Arrays;

import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.PublisherInvoker;

public class PublisherNode<T> extends AbstractNode {
  private PublisherInvoker<T> publisherInvoker;

  public PublisherNode(String name, PublisherInvoker<T> publisherInvoker, String... outputChannels) {
    super(name);
    this.publisherInvoker = publisherInvoker;
    if (outputChannels != null) {
      Arrays.stream(outputChannels)
          .forEach(channelName -> this.children.put(channelName, null));
    }
  }

  public PublisherInvoker<T> getPublisherInvoker() {
    return publisherInvoker;
  }
}
