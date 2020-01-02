package com.amadeus.middleware.odyssey.reactive.messaging.core.impl;

import java.lang.reflect.Method;

import com.amadeus.middleware.odyssey.reactive.messaging.core.FunctionInvokerBuilder;

public abstract class AbstractFunctionInvokerBuilder implements FunctionInvokerBuilder {

  protected Class<?> targetClass;
  protected Method targetMethod;
  protected Object defaultTargetInstance;

  @Override
  public FunctionInvokerBuilder targetClass(Class<?> targetClass) {
    this.targetClass = targetClass;
    return this;
  }

  @Override
  public FunctionInvokerBuilder targetMethod(Method targetMethod) {
    this.targetMethod = targetMethod;
    return this;
  }

  @Override
  public FunctionInvokerBuilder defaultTargetInstance(Object defaultTargetInstance) {
    this.defaultTargetInstance = defaultTargetInstance;
    return this;
  }
}
