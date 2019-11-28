package com.amadeus.middleware.odyssey.reactive.messaging.core.cdiextension;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Async;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.MessageImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amadeus.middleware.odyssey.reactive.messaging.core.AsyncResolutionException;
import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.FunctionInvocationException;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.FunctionInvoker;

public class CDIFunctionInvoker implements FunctionInvoker {
  private static final Logger logger = LoggerFactory.getLogger(CDIFunctionInvoker.class);

  private BeanManager beanManager;
  private Class targetClass;
  private Method targetMethod;

  public CDIFunctionInvoker(BeanManager beanManager, Class targetClass, Method targetMethod) {
    this.targetClass = targetClass;
    this.beanManager = beanManager;
    this.targetMethod = targetMethod;
  }

  public Class getTargetClass() {
    return targetClass;
  }

  public Object invoke(Object targetInstance, Message message) throws FunctionInvocationException {
    MessageImpl messageImpl = (MessageImpl) message;
    MessageScopedContext context = MessageScopedContext.getInstance();
    context.start(messageImpl.getScopeContextId());
    try {
      Object[] parameters = buildParameters(message);
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
    Instance<Object> instance = beanManager.createInstance();
    List<Object> parameters = new ArrayList<>();
    for (Parameter param : targetMethod.getParameters()) {

      // Special handling of Async as it is a parameterized type
      if (Async.class.isAssignableFrom(param.getType())) {
        Type type = param.getParameterizedType();
        ArrayList<Annotation> annotations = new ArrayList<Annotation>();
        if (param.getAnnotations() != null) {
          Arrays.stream(param.getAnnotations())
              .forEach(a -> annotations.add(a));
        }
        annotations.add(new TypeAnnotationLiteral(type.getTypeName()));
        Instance asyncInstance = instance.select(new TypeAnnotationLiteral(type.getTypeName()))
            .select(param.getType());
        if (asyncInstance.isResolvable()) {
          parameters.add(asyncInstance.get());
        } else {
          throw new AsyncResolutionException("Cannot resolve parameter for " + targetMethod.toString() + ": "
              + type.getTypeName() + " " + param.getName());
        }
        continue;
      }

      // Regular CDI case
      Instance<Object> ip = (Instance<Object>) instance.select(param.getType(), param.getAnnotations());
      if (ip.isResolvable()) {
        parameters.add(ip.get());
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
