package com.amadeus.middleware.odyssey.reactive.messaging.core.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Async;
import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageContext;

public class BaseFunctionInvoker implements FunctionInvoker {
  private static final Logger logger = LoggerFactory.getLogger(BaseFunctionInvoker.class);

  private Class<?> targetClass;
  private Method targetMethod;

  public BaseFunctionInvoker(Class<?> targetClass, Method targetMethod) {
    this.targetClass = targetClass;
    this.targetMethod = targetMethod;
  }

  public Class<?> getTargetClass() {
    return targetClass;
  }

  public Object invoke(Object targetInstance, Message<?> message) throws FunctionInvocationException {
    Object[] parameters = buildParameters(message);
    try {
      return targetMethod.invoke(targetInstance, parameters);
    } catch (Exception e) {
      throw new FunctionInvocationException(e);
    }
  }

  private Object[] buildParameters(Message<?> message) {
    List<Object> parameters = new ArrayList<>();
    for (Parameter param : targetMethod.getParameters()) {
      // Special handling of Async as it is a parameterized type
      if (Async.class.isAssignableFrom(param.getType())) {
        ParameterizedType type = (ParameterizedType) param.getParameterizedType();
        Type parameterType = type.getActualTypeArguments()[0];
        if (ParameterizedType.class.isAssignableFrom(parameterType.getClass())) {
          parameterType = ((ParameterizedType) parameterType).getRawType();
        }
        Class<?> clazz = (Class<?>) parameterType;
        parameters.add(new DirectAsync<>(BaseFunctionInvoker.get(message, clazz)));
        continue;
      }

      // Message Scoped object
      Class<?> clazz = (Class<?>) param.getType();
      Object object = BaseFunctionInvoker.get(message, clazz);
      if (object != null) {
        parameters.add(object);
        continue;
      }

      // Try payload resolution
      Object payload = message.getPayload();
      if ((payload != null) && (param.getType()
          .isAssignableFrom(payload.getClass()))) {
        parameters.add(payload);
        continue;
      }

      // Here we have nothing as a parameter, let's use null
      // TODO: Or should it send an exception and kill the stream?
      logger.warn("null parameter injections for {}.{} {} {} with type={}", targetClass, targetMethod.getName(),
          param.getType()
              .getName(),
          param.getName(), (payload == null) ? null : payload.getClass());
      parameters.add(null);
    }
    return parameters.toArray();
  }

  @SuppressWarnings("unchecked")
  public static <T> T get(Message message, Class<T> clazz) {
    if (message == null) {
      return null;
    }
    if (Message.class.isAssignableFrom(clazz)) {
      return (T) message;
    }
    Object payload = message.getPayload();
    if ((payload != null) && (payload.getClass()
        .isAssignableFrom(clazz))) {
      return (T) payload;
    }
    if (MessageContext.class.isAssignableFrom(clazz)) {
      Iterable<MessageContext> it = message.getContexts();
      for (MessageContext mc : it) {
        if (clazz.isAssignableFrom(mc.getClass())) {
          return (T) mc;
        }
      }
    }
    return null;
  }
}
