package com.amadeus.middleware.odyssey.reactive.messaging.core.cdiextension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageContext;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.MessageContextFactory;

public class MessageContextFactoryImpl implements MessageContextFactory {
  private static final Logger logger = LoggerFactory.getLogger(MessageContextFactoryImpl.class);

  private static class InvokationTarget {
    private Class<?> factoryClass;
    private Method method;
    private Provider<?> messageContextFactoryInstance;

    public InvokationTarget(Class<?> clazz, Method method) {
      this.factoryClass = clazz;
      this.method = method;
    }

    public MessageContext invoke() throws InvocationTargetException, IllegalAccessException {
      return (MessageContext) method.invoke(messageContextFactoryInstance.get());
    }

    public Class<?> getFactoryClass() {
      return factoryClass;
    }
  }

  private Map<Class<? extends MessageContext>, InvokationTarget> invokationTargets = new ConcurrentHashMap<>();

  public void initialize(BeanManager beanManager) {
    logger.debug("initialize");
    Instance<Object> instance = beanManager.createInstance();
    for (InvokationTarget invokationTarget : invokationTargets.values()) {
      invokationTarget.messageContextFactoryInstance = instance.select(invokationTarget.getFactoryClass());
    }
  }

  @Override
  public void add(Class<? extends MessageContext> returnType, Class<?> factoryClass, Method builder) {
    invokationTargets.put(returnType, new InvokationTarget(factoryClass, builder));
  }

  @Override
  public Set<Class<? extends MessageContext>> getMessageContext() {
    return invokationTargets.keySet();
  }

  @Override
  public MessageContext create(Class<? extends MessageContext> type)
      throws InvocationTargetException, IllegalAccessException {
    return invokationTargets.get(type)
        .invoke();
  }
}
