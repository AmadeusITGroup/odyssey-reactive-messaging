package com.amadeus.middleware.odyssey.reactive.messaging.core.cdiextension;

import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageInitializer;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BasicContextFactory {

  @MessageInitializer
  public BasicContext build() {
    return new BasicContextImpl();
  }
}
