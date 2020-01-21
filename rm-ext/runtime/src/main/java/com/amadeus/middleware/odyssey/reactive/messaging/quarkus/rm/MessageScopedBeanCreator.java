package com.amadeus.middleware.odyssey.reactive.messaging.quarkus.rm;

import java.util.Map;

import javax.enterprise.context.spi.CreationalContext;

import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.cdi.ProxyProducer;

import io.quarkus.arc.BeanCreator;

public class MessageScopedBeanCreator implements BeanCreator<Object> {
  public final static String CLASS_KEY = "CLASS_KEY";

  @SuppressWarnings("unchecked")
  @Override
  public Object create(CreationalContext<Object> creationalContext, Map<String, Object> params) {
    ProxyProducer pp = new ProxyProducer((Class<?>) params.get(CLASS_KEY));
    return pp.apply(creationalContext);
  }
}
