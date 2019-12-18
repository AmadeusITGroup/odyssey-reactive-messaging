package com.amadeus.middleware.odyssey.reactive.messaging.core.cdiextension;

import java.lang.reflect.Proxy;
import java.util.function.Function;

import javax.enterprise.context.spi.CreationalContext;

public class ProxyProducer<T> implements Function<CreationalContext<T>, T> {
  private Class<?> clazz;

  public ProxyProducer(Class<?> clazz) {
    this.clazz = clazz;
  }

  @SuppressWarnings("unchecked")
  @Override
  public T apply(CreationalContext<T> creationalContext) {
    return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] { clazz }, new MessageScopedInvocationHandler(clazz));
  }
}
