package com.amadeus.middleware.odyssey.reactive.messaging.core.cdiextension;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.BaseFunctionInvoker;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.FunctionInvocationException;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.MessageInitializerRegistry;

public class MessageInitializerRegistryImpl implements MessageInitializerRegistry {
  private static final Logger logger = LoggerFactory.getLogger(MessageInitializerRegistryImpl.class);

  private static class InvokationTarget {
    private BaseFunctionInvoker functionInvoker;
    private Provider<?> messageContextFactoryInstance;

    public InvokationTarget(Class<?> clazz, Method method) {
      functionInvoker = new BaseFunctionInvoker(clazz, method);
    }

    public void invoke(Message message) throws FunctionInvocationException {
      functionInvoker.invoke(messageContextFactoryInstance.get(), message);
    }

    public Class<?> getFactoryClass() {
      return functionInvoker.getTargetClass();
    }

    public Method getMethod() {
      return functionInvoker.getMethod();
    }
  }

  private List<InvokationTarget> invokationTargets = new ArrayList<>();

  public void initialize(BeanManager beanManager) {
    logger.debug("initialize");
    Instance<Object> instance = beanManager.createInstance();
    for (InvokationTarget invokationTarget : invokationTargets) {
      invokationTarget.messageContextFactoryInstance = instance.select(invokationTarget.getFactoryClass());
    }
  }

  @Override
  public void add(Class<?> factoryClass, Method builder) {
    invokationTargets.add(new InvokationTarget(factoryClass, builder));
  }

  @Override
  public void initialize(Message message) throws FunctionInvocationException {
    for (InvokationTarget initalizer : invokationTargets) {
      try {
        initalizer.invoke(message);
      } catch (Exception e) {
        logger.error("Exception in the call of {}.{}", initalizer.getFactoryClass()
            .getSimpleName(),
            initalizer.getMethod()
                .getName());
        throw e;
      }
    }
  }
}
