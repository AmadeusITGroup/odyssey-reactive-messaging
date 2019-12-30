package com.amadeus.middleware.odyssey.reactive.messaging.core.impl;

import java.lang.reflect.Method;
import java.util.Set;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageContext;

public interface MessageInitializerRegistry {

  void add(Class<?> factoryClass, Method builder);

  void initialize(Message message) throws FunctionInvocationException;
}
