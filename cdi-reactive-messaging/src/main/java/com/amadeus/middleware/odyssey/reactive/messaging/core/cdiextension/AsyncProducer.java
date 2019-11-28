package com.amadeus.middleware.odyssey.reactive.messaging.core.cdiextension;

import java.util.Objects;
import java.util.function.Function;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.BeanManager;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Async;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageContext;

public class AsyncProducer implements Function<CreationalContext<Async>, Async> {

  private BeanManager beanManager;

  private Class<? extends MessageContext> clazz;

  public AsyncProducer(BeanManager beanManager, Class<? extends MessageContext> clazz) {
    Objects.requireNonNull(beanManager);
    Objects.requireNonNull(clazz);
    this.beanManager = beanManager;
    this.clazz = clazz;
  }

  @Override
  public Async apply(CreationalContext<Async> cc) {
    return new CDIAsync<>(clazz, beanManager);
  }
}
