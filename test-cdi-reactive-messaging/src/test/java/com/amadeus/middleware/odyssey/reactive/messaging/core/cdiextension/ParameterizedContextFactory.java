package com.amadeus.middleware.odyssey.reactive.messaging.core.cdiextension;

import javax.enterprise.context.ApplicationScoped;

import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageInitializer;

@ApplicationScoped
public class ParameterizedContextFactory {

  @SuppressWarnings("rawtypes")
  @MessageInitializer
  public ParameterizedContext build() {
    return new ParameterizedContextImpl();
  }
}
