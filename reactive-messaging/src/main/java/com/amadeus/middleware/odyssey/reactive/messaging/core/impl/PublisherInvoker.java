package com.amadeus.middleware.odyssey.reactive.messaging.core.impl;

import java.lang.reflect.Method;

import org.reactivestreams.Publisher;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;

public class PublisherInvoker<T> {

  private Class<?> targetClass;
  private Method targetMethod;

  public PublisherInvoker(Class<?> targetClass, Method targetMethod) {
    this.targetClass = targetClass;
    this.targetMethod = targetMethod;
  }

  public Class<?> getTargetClass() {
    return targetClass;
  }

  @SuppressWarnings("unchecked")
  public Publisher<Message<T>> invoke(Object targetInstance) throws FunctionInvocationException {
    try {
      return (Publisher<Message<T>>) targetMethod.invoke(targetInstance);
    } catch (Exception e) {
      throw new FunctionInvocationException(e);
    }
  }
}
