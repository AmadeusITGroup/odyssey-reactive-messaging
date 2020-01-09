package com.amadeus.middleware.odyssey.reactive.messaging.quarkus.rm;

import javax.enterprise.context.spi.CreationalContext;
import java.util.Map;

public interface MessageContextCreator<T> {
  Object create(Class<?> clazz, CreationalContext<Object> creationalContext, Map<String, Object> params);
}
