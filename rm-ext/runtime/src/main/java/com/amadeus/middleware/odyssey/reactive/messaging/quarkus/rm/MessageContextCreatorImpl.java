package com.amadeus.middleware.odyssey.reactive.messaging.quarkus.rm;

import java.util.Map;

import javax.enterprise.context.spi.CreationalContext;

import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.cdi.ProxyProducer;

public class MessageContextCreatorImpl<T> implements MessageContextCreator<T> {

  @SuppressWarnings("unchecked")
  @Override
  public Object create(Class<?> clazz, CreationalContext<Object> creationalContext,  Map<String, Object> params) {
    ProxyProducer pp = new ProxyProducer<>(clazz);
    return pp.apply(creationalContext);
  }
}
