package com.amadeus.middleware.odyssey.reactive.messaging.core.topology;

import java.util.Arrays;
import java.util.Optional;

import org.reactivestreams.Subscriber;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;

public class SubscriberNode<T> extends AbstractNode {
  private Subscriber<Message<? super T>> subscriber;

  public SubscriberNode() {
  }

  public SubscriberNode(String name, Subscriber<Message<? super T>> subscriber, String[] channelNames) {
    super(name);
    this.subscriber = subscriber;
    if (channelNames != null) {
      Arrays.stream(channelNames)
          .forEach(channelName -> this.parents.put(channelName, Optional.empty()));
    }
  }

  public SubscriberNode(SubscriberNode<T> that) {
    this(that.name, that.subscriber, that.parents.keySet()
      .toArray(new String[] {}));
  }

  public Subscriber<Message<? super T>> getSubscriber() {
    return subscriber;
  }
}
