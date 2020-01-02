package com.amadeus.middleware.odyssey.reactive.messaging.core.impl;

import java.util.ServiceLoader;

import com.amadeus.middleware.odyssey.reactive.messaging.core.FunctionInvokerBuilder;

public class FunctionInvokerBuilderProviderProvider {
  private static ServiceLoader<FunctionInvokerBuilderProvider> serviceLoader = ServiceLoader
      .load(FunctionInvokerBuilderProvider.class);
  private static FunctionInvokerBuilderProvider provider;

  static {
    provider = serviceLoader.iterator()
        .next();
  }

  @SuppressWarnings("rawtypes")
  public static FunctionInvokerBuilder create() {
    return provider.build();
  }
}
