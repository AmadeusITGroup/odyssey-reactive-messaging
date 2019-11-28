package com.amadeus.middleware.odyssey.reactive.messaging.core.impl;

import java.lang.reflect.Method;

import org.reactivestreams.Publisher;

public class PublisherInvoker {

  private Class targetClass;
  private Method targetMethod;

  public PublisherInvoker(Class targetClass, Method targetMethod) {
    this.targetClass = targetClass;
    this.targetMethod = targetMethod;
  }

  public Class getTargetClass() {
    return targetClass;
  }

  public Publisher invoke(Object targetInstance) throws FunctionInvocationException {
    try {
      return (Publisher) targetMethod.invoke(targetInstance);
    } catch (Exception e) {
      throw new FunctionInvocationException(e);
    }
  }
}
