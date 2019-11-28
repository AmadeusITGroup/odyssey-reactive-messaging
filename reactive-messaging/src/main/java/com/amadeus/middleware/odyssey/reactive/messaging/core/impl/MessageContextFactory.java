package com.amadeus.middleware.odyssey.reactive.messaging.core.impl;

import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

public interface MessageContextFactory {
  void add(Class returnType, Method builder);

  void add(Class returnType, Class factoryClass);

  Set<Class> getMessageContext();

  MessageContext create(Class type) throws InvocationTargetException, IllegalAccessException;
}
