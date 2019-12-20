package com.amadeus.middleware.odyssey.reactive.messaging.core.cdiextension;

import javax.enterprise.context.ApplicationScoped;

import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageContextBuilder;

@ApplicationScoped
public class ParameterizedContextFactory {

  @SuppressWarnings("rawtypes")
  @MessageContextBuilder
  public ParameterizedContext build() {
    return new ParameterizedContextImpl();
  }
}
