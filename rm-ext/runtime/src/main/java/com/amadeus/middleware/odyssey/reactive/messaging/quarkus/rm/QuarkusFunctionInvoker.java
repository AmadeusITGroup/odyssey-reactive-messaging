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

import com.amadeus.middleware.odyssey.reactive.messaging.core.FunctionInvoker;
import com.amadeus.middleware.odyssey.reactive.messaging.core.Invoker;
import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.FunctionInvocationException;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.MessageImpl;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.cdi.MessageScopedContext;

import io.quarkus.arc.Arc;

public class QuarkusFunctionInvoker implements FunctionInvoker {
  private static final Logger logger = Logger.getLogger(QuarkusFunctionInvoker.class);

  private String beanId;
  private String methodName;

  private Type[] parameterTypes;
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

  public Bean<?> getBean() {
    return Arc.container()
        .bean(beanId);
  }

  public String getMethodName() {
    return methodName;
  }

  public void setMethodName(String methodName) {
    this.methodName = methodName;
  }

  @Override
  public Class<?> getTargetClass() {
    return targetClass;
  }

  @Override
  public Method getMethod() {
    return null;
  }

  @Override
  public Signature getSignature() {
    return signature;
  }

  public void setSignature(Signature signature) {
    this.signature = signature;
  }

  @Override
  public void setTargetInstance(Object targetInstance) {
    this.targetInstance = targetInstance;
  }

  @Override
  public Object getTargetInstance() {
    return targetInstance;
  }

  public Type[] getParameterTypes() {
    return parameterTypes;
  }

  public void setParameterTypes(Type[] parameterTypes) {
    this.parameterTypes = parameterTypes;
  }

  @Override
  public Object invoke(Object targetInstance, Message<?> message) throws FunctionInvocationException {
    return null;
  }

  @Override
  public Object invoke(Message<?> message) throws FunctionInvocationException {

    MessageImpl<?> messageImpl = (MessageImpl<?>) message;
    MessageScopedContext context = MessageScopedContext.getInstance();
    context.start(messageImpl.getScopeContextId());
    try {
      return invoker.invoke(buildParameters(message));
    } catch (Exception e) {
      throw new FunctionInvocationException(e);
    } finally {
      context.suspend();
    }
  }

  private Object[] buildParameters(Message<?> message) {
    List<Object> parameters = new ArrayList<>();
    for (Type paramType : parameterTypes) {
      // Special handling of Async as it is a parameterized type
      /*
       * if (Async.class.isAssignableFrom(param.getType())) { ParameterizedType type = (ParameterizedType)
       * param.getParameterizedType(); Type parameterType = type.getActualTypeArguments()[0]; if
       * (ParameterizedType.class.isAssignableFrom(parameterType.getClass())) { parameterType = ((ParameterizedType)
       * parameterType).getRawType(); } Class<?> clazz = (Class<?>) parameterType; parameters.add(new
       * DirectAsync<>(MessageImpl.get(message, clazz))); continue; }
       */

      // Message Scoped object
      Class<?> clazz = (Class<?>) paramType;
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

  @Override
  public Publisher<Message<?>> invoke(Object targetInstance, PublisherBuilder<Message<?>> publisherBuilder)
      throws FunctionInvocationException {
    return null;
  }

  @SuppressWarnings("unchecked")
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
