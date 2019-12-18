package com.amadeus.middleware.odyssey.reactive.messaging.core.impl;

import java.util.ServiceLoader;

import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageBuilder;

public class MessageBuilderProviderProvider {
  private static ServiceLoader<MessageBuilderProvider> serviceLoader = ServiceLoader.load(MessageBuilderProvider.class);
  private static MessageBuilderProvider provider;

  static {
    provider = serviceLoader.iterator()
        .next();
  }

  @SuppressWarnings("rawtypes")
  public static MessageBuilder create() {
    return provider.build();
  }
}
