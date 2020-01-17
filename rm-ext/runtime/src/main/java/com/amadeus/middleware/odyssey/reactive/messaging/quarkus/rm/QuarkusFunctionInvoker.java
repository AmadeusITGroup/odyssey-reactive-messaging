package com.amadeus.middleware.odyssey.reactive.messaging.quarkus.rm;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.jboss.logging.Logger;
import org.reactivestreams.Publisher;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Async;
import com.amadeus.middleware.odyssey.reactive.messaging.core.FunctionInvoker;
import com.amadeus.middleware.odyssey.reactive.messaging.core.Invoker;
import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.DirectAsync;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.FunctionInvocationException;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.MessageImpl;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.cdi.MessageScopedContext;

import io.quarkus.arc.Arc;

public class QuarkusFunctionInvoker implements FunctionInvoker {
  private static final Logger logger = Logger.getLogger(QuarkusFunctionInvoker.class);

  private String beanId;
  private String methodName;

  private Type[] parameterTypes;
  private Type[] asyncParameterTypes;
  private Signature signature;
  private Class<?> targetClass;
  private Object targetInstance;

  private Invoker invoker;

  private Class<? extends Invoker> invokerClass;

  public Class<? extends Invoker> getInvokerClass() {
    return invokerClass;
  }

  public void setInvokerClass(Class<? extends Invoker> invokerClass) {
    this.invokerClass = invokerClass;
  }

  public QuarkusFunctionInvoker() {
  }

  public void setBeanId(String beanId) {
    this.beanId = beanId;
  }

  public String getBeanId() {
    return beanId;
  }

  public void setMethodName(String methodName) {
    this.methodName = methodName;
  }

  @Override
  public Signature getSignature() {
    return signature;
  }

  public void setSignature(Signature signature) {
    this.signature = signature;
  }

  public void setParameterTypes(Type[] parameterTypes) {
    this.parameterTypes = parameterTypes;
  }

  public Type[] getParameterTypes() {
    return parameterTypes;
  }

  public void setAsyncParameterTypes(Type[] asyncParameterTypes) {
    this.asyncParameterTypes = asyncParameterTypes;
  }

  public Type[] getAsyncParameterTypes() {
    return asyncParameterTypes;
  }

  @Override
  public Object invoke(Message<?> message) throws FunctionInvocationException {
    MessageImpl<?> messageImpl = (MessageImpl<?>) message;
    MessageScopedContext context = MessageScopedContext.getInstance();
    boolean alreadyActive = context.isActive();
    if (!alreadyActive) {
      context.start(messageImpl.getScopeContextId());
    }
    try {
      return invoker.invoke(buildParameters(message));
    } catch (Exception e) {
      throw new FunctionInvocationException(e);
    } finally {
      if (!alreadyActive) {
        context.suspend();
      }
    }
  }

  @Override
  public Object invoke(PublisherBuilder<Message<?>> publisher) throws FunctionInvocationException {
    return invoker.invoke(publisher.buildRs());
  }

  private Object[] buildParameters(Message<?> message) {
    List<Object> parameters = new ArrayList<>();
    int asyncIndex = 0;
    for (Type paramType : parameterTypes) {

      Class<?> clazz = (Class<?>) paramType;

      // Special handling of Async as it is a parameterized type
      if (Async.class.isAssignableFrom(clazz)) {
        parameters.add(new DirectAsync<>(MessageImpl.get(message, (Class<?>) asyncParameterTypes[asyncIndex])));
        asyncIndex++;
        continue;
      }

      // Message Scoped object
      Object object = MessageImpl.get(message, clazz);
      if (object != null) {
        parameters.add(object);
        continue;
      }

      // Try payload resolution
      Object payload = message.getPayload();
      if ((payload != null) && (clazz.isAssignableFrom(payload.getClass()))) {
        parameters.add(payload);
        continue;
      }

      // Here we have nothing as a parameter, let's use null
      // TODO: Or should it send an exception and kill the stream?
      logger.warnf("null parameter injections for %s.%s %s %s with type=%s", targetClass, methodName, clazz.getName(),
          clazz.getName(), (payload == null) ? null : payload.getClass());
      parameters.add(null);
    }
    return parameters.toArray();
  }


  @SuppressWarnings("unchecked")
  @Override
  public void initialize() {
    Bean bean = Arc.container()
        .bean(getBeanId());
    BeanManager beanManager = Arc.container()
        .beanManager();
    targetInstance = beanManager.getReference(bean, Object.class, beanManager.createCreationalContext(bean));

    targetClass = bean.getBeanClass();

    if (getInvokerClass() != null) {
      try {
        Constructor<? extends Invoker> constructorUsingBeanInstance = getInvokerClass().getConstructor(Object.class);
        if (constructorUsingBeanInstance != null) {
          invoker = constructorUsingBeanInstance.newInstance(targetInstance);
        } else {
          invoker = getInvokerClass().newInstance();
        }
      } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
        logger.error("Unable to create invoker instance of " + getInvokerClass(), e);
        return;
      }
    }
  }
}
