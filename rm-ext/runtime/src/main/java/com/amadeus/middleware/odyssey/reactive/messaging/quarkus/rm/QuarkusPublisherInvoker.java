package com.amadeus.middleware.odyssey.reactive.messaging.quarkus.rm;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.logging.Logger;
import org.reactivestreams.Publisher;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Invoker;
import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.FunctionInvocationException;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.PublisherInvoker;

import io.quarkus.arc.Arc;

public class QuarkusPublisherInvoker<T> implements PublisherInvoker<T> {
  private static final Logger logger = Logger.getLogger(QuarkusPublisherInvoker.class);

  private String beanId;
  private Class<? extends Invoker> invokerClass;

  private Invoker invoker;

  public String getBeanId() {
    return beanId;
  }

  public void setBeanId(String beanId) {
    this.beanId = beanId;
  }

  public Class<? extends Invoker> getInvokerClass() {
    return invokerClass;
  }

  public void setInvokerClass(Class<? extends Invoker> invokerClass) {
    this.invokerClass = invokerClass;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Publisher<Message<T>> invoke() throws FunctionInvocationException {
    return (Publisher<Message<T>>) invoker.invoke(new Object[] {});
  }

  @SuppressWarnings("unchecked")
  public void initialize() {
    Bean bean = Arc.container()
        .bean(getBeanId());
    BeanManager beanManager = Arc.container()
        .beanManager();
    Object targetInstance = beanManager.getReference(bean, Object.class, beanManager.createCreationalContext(bean));
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
