package com.amadeus.middleware.odyssey.reactive.messaging.core.cdiextension;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ContextNotActiveException;

public final class MessageScopedContext {

  private static final MessageScopedContext INSTANCE = new MessageScopedContext();

  public static MessageScopedContext getInstance() {
    return INSTANCE;
  }

  private static final ThreadLocal<String> ACTIVE_MESSAGE_SCOPE_THREAD_LOCAL = ThreadLocal.withInitial(() -> null);

  private final ConcurrentHashMap<String, Map<Class<?>, Object>> cache = new ConcurrentHashMap<>();

  public void start(String scopeId) {
    Objects.requireNonNull(scopeId);
    if (ACTIVE_MESSAGE_SCOPE_THREAD_LOCAL.get() != null) {
      throw new IllegalStateException("An instance of the scope is already active");
    }
    ACTIVE_MESSAGE_SCOPE_THREAD_LOCAL.set(scopeId);
  }

  public void suspend() {
    if (ACTIVE_MESSAGE_SCOPE_THREAD_LOCAL.get() == null) {
      throw new IllegalStateException("Scope not currently active!");
    }
    ACTIVE_MESSAGE_SCOPE_THREAD_LOCAL.set(null);
  }

  public void destroy(String scopeId) {
    Objects.requireNonNull(scopeId);
    if (cache.remove(scopeId) == null) {
      throw new IllegalStateException("Destroying an unknown scopeId=" + scopeId);
    }
  }

  public void add(String scopeId, Class<?> clazz, Object object) {
    cache.computeIfAbsent(scopeId, key -> new ConcurrentHashMap<>())
        .computeIfAbsent(clazz, key -> object);
  }

  public <T> T get(Class<T> clazz) {
    String scopeId = ACTIVE_MESSAGE_SCOPE_THREAD_LOCAL.get();
    if (scopeId == null) {
      throw new ContextNotActiveException();
    }
    Map<Class<?>, Object> map = cache.get(scopeId);
    if (map == null) {
      return null;
    }
    Object object = map.get(clazz);
    if (object == null) {
      return null;
    }
    return (T) object;
  }

  public boolean isActive() {
    return ACTIVE_MESSAGE_SCOPE_THREAD_LOCAL.get() != null;
  }
}
