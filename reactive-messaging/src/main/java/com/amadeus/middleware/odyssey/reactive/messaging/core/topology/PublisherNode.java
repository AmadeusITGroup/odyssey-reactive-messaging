package com.amadeus.middleware.odyssey.reactive.messaging.core.topology;

import java.util.Arrays;
import java.util.Optional;

import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.PublisherInvoker;

public class PublisherNode<T> extends AbstractNode {
  private PublisherInvoker<T> publisherInvoker;

  public PublisherNode() {
  }

  public PublisherNode(PublisherNode<T> that) {
    this(that.name, that.publisherInvoker, that.children.keySet()
        .toArray(new String[] {}));
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
}
