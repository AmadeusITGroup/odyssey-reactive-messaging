package com.amadeus.middleware.odyssey.reactive.messaging.core.cdiextension;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class MessageScopedInvocationHandler implements InvocationHandler {

  Class<?> clazz;

  MessageScopedInvocationHandler(Class<?> clazz) {
    this.clazz = clazz;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    Object object = MessageScopedContext.getInstance()
        .get(clazz);
    return method.invoke(object, args);
  }
}
