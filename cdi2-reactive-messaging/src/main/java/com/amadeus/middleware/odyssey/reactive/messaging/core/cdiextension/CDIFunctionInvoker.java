package com.amadeus.middleware.odyssey.reactive.messaging.core.cdiextension;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Async;
import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.FunctionInvocationException;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.FunctionInvoker;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.MessageImpl;

public class CDIFunctionInvoker implements FunctionInvoker {
  private static final Logger logger = LoggerFactory.getLogger(CDIFunctionInvoker.class);

  private Class targetClass;
  private Method targetMethod;

  public CDIFunctionInvoker(Class targetClass, Method targetMethod) {
    this.targetClass = targetClass;
    this.targetMethod = targetMethod;
  }

  public Class getTargetClass() {
    return targetClass;
  }

  public Object invoke(Object targetInstance, Message message) throws FunctionInvocationException {
    MessageImpl messageImpl = (MessageImpl) message;
    MessageScopedContext context = MessageScopedContext.getInstance();
    context.start(messageImpl.getScopeContextId());
    Object[] parameters = buildParameters(message);
    try {
      return targetMethod.invoke(targetInstance, parameters);
    } catch (Exception e) {
      throw new FunctionInvocationException(e);
    } finally {
      context.suspend();
    }
  }

  // This should be called within the scope of an
  // active MessageContext
  private Object[] buildParameters(Message message) {
    List<Object> parameters = new ArrayList<>();
    MessageScopedContext msc = MessageScopedContext.getInstance();
    for (Parameter param : targetMethod.getParameters()) {

      // Special handling of Async as it is a parameterized type
      if (Async.class.isAssignableFrom(param.getType())) {
        ParameterizedType type = (ParameterizedType) param.getParameterizedType();
        Class<?> clazz = (Class<?>) type.getActualTypeArguments()[0];
        parameters.add(new CDIAsync<>(clazz));
        continue;
      }

      // Message Scoped object
      Class<?> clazz = (Class<?>) param.getType();
      Object object = msc.get(clazz);
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
}
