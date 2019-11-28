package com.amadeus.middleware.odyssey.reactive.messaging.core.cdiextension;

import java.util.Set;

import javax.enterprise.inject.Vetoed;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Async;
import com.amadeus.middleware.odyssey.reactive.messaging.core.AsyncResolutionException;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageScoped;
import com.amadeus.middleware.odyssey.reactive.messaging.core.cdiextension.MessageScopedContext;

@Vetoed
public class CDIAsync<T> implements Async<T> {
  private Class<T> theClass;
  private BeanManager beanManager;

  public CDIAsync() {
  } // To make openwebbeans happy TODO: see if can be removed

  public CDIAsync(Class<T> theClass, BeanManager beanManager) {
    this.theClass = theClass;
    this.beanManager = beanManager;
  }

  public T get() {
    MessageScopedContext context = (MessageScopedContext) beanManager.getContext(MessageScoped.class);
    Set<Bean<?>> bs = beanManager.getBeans(theClass);
    if (bs.isEmpty()) {
      throw new AsyncResolutionException("Cannot find: " + theClass.getName());
    } else if (bs.size() > 1) {
      throw new AsyncResolutionException("Ambiguous resolution: " + theClass.getName());
    }
    Bean mybean = bs.iterator()
        .next();
    // This should create the bean in the context if not already there
    beanManager.createInstance()
        .select(theClass);
    Object mb = context.get(mybean);
    return (T) mb;
  }

}
