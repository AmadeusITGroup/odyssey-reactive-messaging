package com.amadeus.middleware.odyssey.reactive.messaging.core.cdiextension;

import java.util.Objects;

import javax.enterprise.context.ContextNotActiveException;

import org.eclipse.collections.api.map.primitive.MutableLongObjectMap;
import org.eclipse.collections.impl.map.mutable.primitive.MutableLongObjectMapFactoryImpl;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.MessageImpl;

public final class MessageScopedContext {

  private static final MessageScopedContext INSTANCE = new MessageScopedContext();

  public static MessageScopedContext getInstance() {
    return INSTANCE;
  }

  private static final ThreadLocal<Long> ACTIVE_MESSAGE_SCOPE_THREAD_LOCAL = ThreadLocal.withInitial(() -> null);

  private final MutableLongObjectMap<Message<?>> store = MutableLongObjectMapFactoryImpl.INSTANCE.<Message<?>> empty()
      .asSynchronized();

  public void start(Long scopeId) {
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

  public void destroy(Long scopeId) {
    Objects.requireNonNull(scopeId);
    if (store.remove(scopeId) == null) {
      throw new IllegalStateException("Destroying an unknown scopeId=" + scopeId);
    }
  }

  public void add(Long scopeId, Message message) {
    store.put(scopeId, message);
  }

  @SuppressWarnings("unchecked")
  public <T> T get(Class<T> clazz) {
    Long scopeId = ACTIVE_MESSAGE_SCOPE_THREAD_LOCAL.get();
    if (scopeId == null) {
      throw new ContextNotActiveException();
    }
    Message message = store.get(scopeId);
    if (message == null) {
      return null;
    }
    return MessageImpl.get(message, clazz);
  }

  public boolean isActive() {
    return ACTIVE_MESSAGE_SCOPE_THREAD_LOCAL.get() != null;
  }
}
