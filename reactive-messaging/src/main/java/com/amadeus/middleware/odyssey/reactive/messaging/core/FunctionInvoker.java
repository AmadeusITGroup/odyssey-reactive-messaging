package com.amadeus.middleware.odyssey.reactive.messaging.core;

import java.lang.reflect.Method;

import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.reactivestreams.Publisher;

import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.FunctionInvocationException;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.FunctionInvokerBuilderProviderProvider;

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

  /**
   * Optionally set a default target instance.
   */
  void setTargetInstance(Object targetInstance);

  /**
   * @return the optional default target instance
   */
  Object getTargetInstance();

  Object invoke(Object targetInstance, Message<?> message) throws FunctionInvocationException;

  /**
   * Can be used only when a default target instance is set.
   */
  Object invoke(Message<?> message) throws FunctionInvocationException;

  Publisher<Message<?>> invoke(Object targetInstance, PublisherBuilder<Message<?>> publisherBuilder)
      throws FunctionInvocationException;

  static FunctionInvokerBuilder builder() {
    return FunctionInvokerBuilderProviderProvider.create();
  }
}
