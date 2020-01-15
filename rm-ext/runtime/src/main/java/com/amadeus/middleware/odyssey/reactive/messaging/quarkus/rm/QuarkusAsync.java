package com.amadeus.middleware.odyssey.reactive.messaging.quarkus.rm;

import org.jboss.logging.Logger;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Async;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.cdi.MessageScopedContext;

public class QuarkusAsync<T> implements Async<T> {

  private static final Logger logger = Logger.getLogger(QuarkusAsync.class);

  private Class<T> clazz;

  @SuppressWarnings("unchecked")
  public QuarkusAsync(String className) {
    try {
      // I'm using this strategy as I didn't find a better way (that probably exists) to not have issue with
      // classloading:
      this.clazz = (Class<T>) Thread.currentThread()
          .getContextClassLoader()
          .loadClass(className);
    } catch (ClassNotFoundException e) {
      logger.error("QuarkusAsync(String className)", e);
    }
  }

  @Override
  public T get() {
    return MessageScopedContext.getInstance()
        .get(clazz);
  }
}
