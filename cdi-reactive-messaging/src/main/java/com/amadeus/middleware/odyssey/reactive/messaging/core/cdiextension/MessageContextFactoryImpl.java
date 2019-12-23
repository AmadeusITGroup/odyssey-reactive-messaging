package com.amadeus.middleware.odyssey.reactive.messaging.core.cdiextension;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageContext;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.BaseFunctionInvoker;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.FunctionInvocationException;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.MessageContextFactory;

public class MessageContextFactoryImpl implements MessageContextFactory {
  private static final Logger logger = LoggerFactory.getLogger(MessageContextFactoryImpl.class);

  private static class InvokationTarget {
    private BaseFunctionInvoker functionInvoker;
    private Provider<?> messageContextFactoryInstance;

    public InvokationTarget(Class<?> clazz, Method method) {
      functionInvoker = new BaseFunctionInvoker(clazz, method);
    }

    public MessageContext invoke(Message message) throws FunctionInvocationException {
      return (MessageContext) functionInvoker.invoke(messageContextFactoryInstance.get(), message);
    }

    public Class<?> getFactoryClass() {
      return functionInvoker.getTargetClass();
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
  public MessageContext create(Message message, Class<? extends MessageContext> type)
      throws FunctionInvocationException {
    return invokationTargets.get(type)
        .invoke(message);
  }
}
