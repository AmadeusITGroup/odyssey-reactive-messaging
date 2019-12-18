package com.amadeus.middleware.odyssey.reactive.messaging.core.cdiextension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;

import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.MessageContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageContext;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageContextBuilder;

public class MessageContextFactoryImpl implements MessageContextFactory {
  private static final Logger logger = LoggerFactory.getLogger(MessageContextFactoryImpl.class);

  private Map<Class<? extends MessageContext>, Method> builders = new ConcurrentHashMap<>();

  private Map<Class<? extends MessageContext>, Class<?>> factoryClasses = new ConcurrentHashMap<>();

  @Override
  public void add(Class<? extends MessageContext> returnType, Method builder) {
    builders.put(returnType, builder);
  }

  @Override
  public void add(Class<? extends MessageContext> returnType, Class<?> factoryClass) {
    factoryClasses.put(returnType, factoryClass);
  }

  @Override
  public Set<Class<? extends MessageContext>> getMessageContext() {
    return builders.keySet();
  }

  @Override
  public MessageContext create(Class<? extends MessageContext> type) throws InvocationTargetException, IllegalAccessException {
    logger.trace("creation of {}", type.getName());


    // TODO: remove this CDI lookup
    BeanManager beanManager = CDI.current()
        .getBeanManager();
    Instance<Object> instance = beanManager.createInstance();
    Instance<?> theFactory = instance.select(factoryClasses.get(type));

    if (theFactory.isResolvable()) {
      Object tf = theFactory.get();
      for (Method m : tf.getClass()
          .getMethods()) {
        if (m.isAnnotationPresent(MessageContextBuilder.class)) {
          MessageContext pojoMessageContext = (MessageContext) m.invoke(tf);
          return pojoMessageContext;
        }
      }
    }
    // TODO: Throw
    return (MessageContext) builders.get(type)
        .invoke(null);
  }
}
