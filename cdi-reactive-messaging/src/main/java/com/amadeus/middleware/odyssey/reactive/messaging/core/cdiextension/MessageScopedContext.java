package com.amadeus.middleware.odyssey.reactive.messaging.core.cdiextension;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageScoped;

public final class MessageScopedContext implements Context {
  private static final Logger logger = LoggerFactory.getLogger(MessageScopedContext.class);

  private static final MessageScopedContext INSTANCE = new MessageScopedContext();

  public static MessageScopedContext getInstance() {
    return INSTANCE;
  }

  final static class BeanInstance<T> {
    private final T instance;
    private final Contextual<T> contextual;
    private final CreationalContext<T> creationalContext;

    BeanInstance(T instance, Contextual<T> contextual, CreationalContext<T> creationalContext) {
      this.instance = instance;
      this.contextual = contextual;
      this.creationalContext = creationalContext;
    }

    T get() {
      return instance;
    }

    void destroy() {
      contextual.destroy(instance, creationalContext);
    }
  }

  private static final ThreadLocal<String> ACTIVE_MESSAGE_SCOPE_THREAD_LOCAL = ThreadLocal.withInitial(() -> null);

  private static final ThreadLocal<Message> ACTIVE_MESSAGE_THREAD_LOCAL = ThreadLocal.withInitial(() -> null);

  private final ConcurrentHashMap<String, Map<Contextual<?>, BeanInstance<?>>> cache = new ConcurrentHashMap<>();

  private MessageScopedContext() {
    //Executors.newScheduledThreadPool(1)
    //    .scheduleAtFixedRate(() -> logger.debug("amount of scopes={}", cache.size()), 0, 2, TimeUnit.SECONDS);
  }

  @Override
  public Class<? extends Annotation> getScope() {
    return MessageScoped.class;
  }

  public void setMessage(Message message) {
    ACTIVE_MESSAGE_THREAD_LOCAL.set(message);
  }

  public Message getMessage() {
    return ACTIVE_MESSAGE_THREAD_LOCAL.get();
  }

  @Override
  public <T> T get(Contextual<T> contextual, CreationalContext<T> creationalContext) {
    String scopeId = ACTIVE_MESSAGE_SCOPE_THREAD_LOCAL.get();
    if (scopeId == null) {
      throw new ContextNotActiveException();
    }
    logger.debug("contextual={}", contextual);

    T instance = (T) cache.computeIfAbsent(scopeId, s -> new ConcurrentHashMap<>())
        .computeIfAbsent(contextual,
            c -> new BeanInstance<>(contextual.create(creationalContext), contextual, creationalContext))
        .get();
    return instance;
  }

  @Override
  public <T> T get(Contextual<T> contextual) {
    String scopeId = ACTIVE_MESSAGE_SCOPE_THREAD_LOCAL.get();
    if (scopeId == null) {
      throw new ContextNotActiveException();
    }
    Map<Contextual<?>, BeanInstance<?>> map = cache.get(scopeId);
    if (map == null) {
      return null;
    }
    BeanInstance<T> instance = (BeanInstance<T>) map.get(contextual);
    if (instance == null) {
      return null;
    }
    return instance.get();
  }

  public void start(String scopeId) {
    if (ACTIVE_MESSAGE_SCOPE_THREAD_LOCAL.get() != null) {
      throw new IllegalStateException("An instance of the scope is already active");
    }
    ACTIVE_MESSAGE_SCOPE_THREAD_LOCAL.set(scopeId);
  }

  public void destroy(String scopeId) {
    Map<Contextual<?>, BeanInstance<?>> instances = cache.remove(scopeId);
    if (instances != null) {
      instances.values()
          .forEach(BeanInstance::destroy);
    }
  }

  public void suspend() {
    if (ACTIVE_MESSAGE_SCOPE_THREAD_LOCAL.get() == null) {
      throw new IllegalStateException("Scope not currently active!");
    }
    ACTIVE_MESSAGE_SCOPE_THREAD_LOCAL.set(null);
  }

  @Override
  public boolean isActive() {
    return ACTIVE_MESSAGE_SCOPE_THREAD_LOCAL.get() != null;
  }
}
