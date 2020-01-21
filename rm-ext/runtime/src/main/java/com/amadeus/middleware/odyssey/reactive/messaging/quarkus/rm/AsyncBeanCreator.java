package com.amadeus.middleware.odyssey.reactive.messaging.quarkus.rm;

import java.util.Map;

import javax.enterprise.context.spi.CreationalContext;

import io.quarkus.arc.BeanCreator;

public class AsyncBeanCreator implements BeanCreator<Object> {
  public final static String CLASS_NAME_KEY = "CLASS_NAME_KEY";

  @Override
  public Object create(CreationalContext<Object> creationalContext, Map<String, Object> params) {
    return new QuarkusAsync((String) params.get(CLASS_NAME_KEY));
  }
}
