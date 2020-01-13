package com.amadeus.middleware.odyssey.reactive.messaging.core.impl.cdi;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.FunctionInvocationException;

public class MessageScopedInvocationHandler implements InvocationHandler {
  private static final Logger logger = LoggerFactory.getLogger(MessageScopedInvocationHandler.class);

  Class<?> clazz;

  MessageScopedInvocationHandler(Class<?> clazz) {
    this.clazz = clazz;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    Object object = MessageScopedContext.getInstance()
        .get(clazz);
    if (object == null) {
      logger.error("Cannot invoke method={}, as the class={} is not in the current message.", method.getName(),
          clazz.getSimpleName());
      throw new FunctionInvocationException("No " + clazz.getSimpleName() + " in the message");
    }
    return method.invoke(object, args);
  }
}
