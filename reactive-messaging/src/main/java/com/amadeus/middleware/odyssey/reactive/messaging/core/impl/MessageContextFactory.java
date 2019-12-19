package com.amadeus.middleware.odyssey.reactive.messaging.core.impl;

import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

public interface MessageContextFactory {
  void add(Class<? extends MessageContext> returnType, Method builder);

  void add(Class<? extends MessageContext>  returnType, Class<?> factoryClass);

  Set<Class<? extends MessageContext>> getMessageContext();

  MessageContext create(Class<? extends MessageContext>  type) throws InvocationTargetException, IllegalAccessException;
}
