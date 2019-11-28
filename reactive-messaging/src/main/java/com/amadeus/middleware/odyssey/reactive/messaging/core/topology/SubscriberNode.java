package com.amadeus.middleware.odyssey.reactive.messaging.core.topology;

import java.util.Arrays;

import org.reactivestreams.Subscriber;

public class SubscriberNode<T> extends AbstractNode {
  private Subscriber<T> subscriber;

  public SubscriberNode(String name, Subscriber<T> subscriber, String... channelNames) {
    super(name);
    this.subscriber = subscriber;
    if (channelNames != null) {
      Arrays.stream(channelNames)
          .forEach(channelName -> this.parents.put(channelName, null));
    }
  }

  public Subscriber getSubscriber() {
    return subscriber;
  }
}
