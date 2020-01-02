package com.amadeus.middleware.odyssey.reactive.messaging.core.impl;

import java.lang.reflect.Method;

import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.reactivestreams.Publisher;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;

public interface FunctionInvoker {

  enum Signature {
    UNKNOWN,
    /* void function( direct injection ) */
    DIRECT,
    /* Publisher<Message<O>> method(Publisher<Message<I>> publisher) */
    PUBLISHER_PUBLISHER
  }

  Class<?> getTargetClass();

  Method getMethod();

  Signature getSignature();

  Object invoke(Object targetInstance, Message<?> message) throws FunctionInvocationException;

  Publisher<Message<?>> invoke(Object targetInstance, PublisherBuilder<Message<?>> publisherBuilder)
      throws FunctionInvocationException;
}
