package com.amadeus.middleware.odyssey.reactive.messaging.core;

import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;

import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.FunctionInvocationException;

public interface FunctionInvoker {

  enum Signature {
    UNKNOWN,
    /* void function( direct injection ) */
    DIRECT,
    /* Publisher<Message<O>> method(Publisher<Message<I>> publisher) */
    PUBLISHER_PUBLISHER
  }

  Signature getSignature();

  void initialize();

  // For DIRECT
  Object invoke(Message<?> message) throws FunctionInvocationException;

  // FOR PUBLISHER_PUBLISHER
  Object invoke(PublisherBuilder<Message<?>> publisher) throws FunctionInvocationException;
}
