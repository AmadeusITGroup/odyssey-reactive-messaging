package com.amadeus.middleware.odyssey.reactive.messaging.core.impl;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;

public interface FunctionInvoker {
  Class<?> getTargetClass();

  Object invoke(Object targetInstance, Message<?> message) throws FunctionInvocationException;
}
