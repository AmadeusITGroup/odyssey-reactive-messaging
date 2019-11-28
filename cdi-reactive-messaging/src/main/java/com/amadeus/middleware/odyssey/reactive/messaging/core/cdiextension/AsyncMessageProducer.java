package com.amadeus.middleware.odyssey.reactive.messaging.core.cdiextension;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageScoped;

public class AsyncMessageProducer {
  @Produces
  @MessageScoped
  public CDIAsync<Message> producer(BeanManager beanManager) {
    return new CDIAsync<>(Message.class, beanManager);
  }

  /*
   * This producer is needed to call a process method with a parameter of Async<Message> type: As this type is
   * parameterized, the lookup in the CDI container should ideally use javax.enterprise.inject.select(TypeLiteral<U>
   * subtype, Annotation... qualifiers). However it is not possible to create a TypeLiteral with a dynamic type (see:
   * https://issues.redhat.com/browse/CDI-455). Hence, the tip used here is to use the (home made) @TypeAnnotation with
   * the type name as a qualifier value.
   */
  @Produces
  @MessageScoped
  @TypeAnnotation("com.amadeus.middleware.odyssey.reactive.messaging.core.Async<com.amadeus.middleware.odyssey.reactive.messaging.core.Message>")
  public CDIAsync producerForProgrammaticLookup(BeanManager beanManager) {
    return new CDIAsync<>(Message.class, beanManager);
  }
}
