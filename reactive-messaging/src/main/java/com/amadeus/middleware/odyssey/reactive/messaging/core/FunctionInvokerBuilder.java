package com.amadeus.middleware.odyssey.reactive.messaging.core;

import java.lang.reflect.Method;

public interface FunctionInvokerBuilder {
  FunctionInvoker build();

  FunctionInvokerBuilder targetClass(Class<?> targetClass);
  FunctionInvokerBuilder targetMethod(Method targetMethod);

  FunctionInvokerBuilder defaultTargetInstance(Object defaultTargetInstance);
}
