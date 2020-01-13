package com.amadeus.middleware.odyssey.reactive.messaging.quarkus.rm;

import java.util.Map;

import javax.enterprise.context.spi.CreationalContext;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.cdi.ProxyProducer;

import io.quarkus.arc.BeanCreator;

public final class MessageCreator implements BeanCreator<Object> {

  @SuppressWarnings("unchecked")
  @Override
  public Object create(CreationalContext<Object> creationalContext, Map<String, Object> params) {
    ProxyProducer pp = new ProxyProducer<>(Message.class);
    return pp.apply(creationalContext);
  }
}
