package com.amadeus.middleware.odyssey.reactive.messaging.core.cdiextension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;

import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.MessageContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageContext;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageContextBuilder;

public class MessageContextFactoryImpl implements MessageContextFactory {
  private static final Logger logger = LoggerFactory.getLogger(MessageContextFactoryImpl.class);

  private Map<Class, Method> builders = new ConcurrentHashMap<>();

  private Map<Class, Class> factoryClasses = new ConcurrentHashMap<>();

  private BeanManager beanManager;

  MessageContextFactoryImpl(BeanManager beanManager) {
    this.beanManager = beanManager;
  }

  public void add(Class returnType, Method builder) {
    builders.put(returnType, builder);
  }

  public void add(Class returnType, Class factoryClass) {
    factoryClasses.put(returnType, factoryClass);
  }

  public Set<Class> getMessageContext() {
    return builders.keySet();
  }

  public MessageContext create(Class type)
      throws InvocationTargetException, IllegalAccessException {
    logger.trace("creation of {}", type.getName());
    Instance<Object> instance = beanManager.createInstance();
    Instance<Object> theFactory = instance.select(factoryClasses.get(type));
    Instance<Message> message = instance.select(Message.class);
    if (theFactory.isResolvable()) {
      Object tf = theFactory.get();
      for (Method m : tf.getClass()
          .getMethods()) {
        if (m.isAnnotationPresent(MessageContextBuilder.class)) {
          MessageContext pojoMessageContext = (MessageContext) m.invoke(tf);
          message.get()
              .addContext(pojoMessageContext);
          return pojoMessageContext;
        }
      }
    }
    // TODO: Throw
    return (MessageContext) builders.get(type)
        .invoke(null);
  }
}
