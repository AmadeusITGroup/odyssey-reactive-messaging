package com.amadeus.middleware.odyssey.reactive.messaging.core.impl;

import java.lang.reflect.Method;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;

import org.reactivestreams.Publisher;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;

public class ReflectivePublisherInvoker<T> implements PublisherInvoker<T> {

  private Class<?> targetClass;
  private Method targetMethod;
  private Object targetInstance;

  public ReflectivePublisherInvoker(Class<?> targetClass, Method targetMethod) {
    this.targetClass = targetClass;
    this.targetMethod = targetMethod;
  }

  public Class<?> getTargetClass() {
    return targetClass;
  }

  public void setTargetInstance(Object targetInstance) {
    this.targetInstance = targetInstance;
  }

  @SuppressWarnings("unchecked")
  public Publisher<Message<T>> invoke() throws FunctionInvocationException {
    try {
      return (Publisher<Message<T>>) targetMethod.invoke(targetInstance);
    } catch (Exception e) {
      throw new FunctionInvocationException(e);
    }
  }

  @Override
  public void initialize() {
    Instance<Object> instance = CDI.current()
      .getBeanManager()
      .createInstance();
    setTargetInstance(instance.select(getTargetClass()).get());
  }
}
