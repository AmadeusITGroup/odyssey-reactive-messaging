package com.amadeus.middleware.odyssey.reactive.messaging.core.cdiextension;

import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageContextBuilder;
import com.amadeus.middleware.odyssey.reactive.messaging.corporate.framework.EventContext;
import com.amadeus.middleware.odyssey.reactive.messaging.corporate.framework.EventContextImpl;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BasicContextFactory {

  @MessageContextBuilder
  public BasicContext build() {
    return new BasicContextImpl();
  }
}
