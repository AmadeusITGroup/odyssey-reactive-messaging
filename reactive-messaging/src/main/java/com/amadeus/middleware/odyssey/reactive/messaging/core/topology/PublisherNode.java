package com.amadeus.middleware.odyssey.reactive.messaging.core.topology;

import java.util.Arrays;
import java.util.Optional;

import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.PublisherInvoker;

public class PublisherNode<T> extends AbstractNode {
  private PublisherInvoker<T> publisherInvoker;

  public PublisherNode() {
  }

  public PublisherNode(String name, PublisherInvoker<T> publisherInvoker, String[] outputChannels) {
    super(name);
    this.publisherInvoker = publisherInvoker;
    if (outputChannels != null) {
      Arrays.stream(outputChannels)
          .forEach(channelName -> this.children.put(channelName, Optional.empty()));
    }
  }

  public void setPublisherInvoker(PublisherInvoker<T> publisherInvoker) {
    this.publisherInvoker = publisherInvoker;
  }

  public PublisherInvoker<T> getPublisherInvoker() {
    return publisherInvoker;
  }

  @Override
  protected Object clone() {
    String[] outputChannels = children.keySet()
        .toArray(new String[] {});
    return new PublisherNode<>(name, publisherInvoker, outputChannels);
  }
}
