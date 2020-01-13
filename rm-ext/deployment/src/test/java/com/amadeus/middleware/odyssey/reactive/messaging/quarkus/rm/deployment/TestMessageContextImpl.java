package com.amadeus.middleware.odyssey.reactive.messaging.quarkus.rm.deployment;

import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageContext;

public class TestMessageContextImpl implements TestMessageContext {
  private String text;

  TestMessageContextImpl(String text) {
    this.text = text;
  }

  @Override
  public String getText() {
    return text;
  }

  @Override
  public String getContextKey() {
    return "X";
  }

  @Override
  public boolean isPropagable() {
    return false;
  }

  @Override
  public String getContextMergeKey() {
    return null;
  }

  @Override
  public MessageContext merge(MessageContext... messageContexts) {
    return null;
  }

  @Override
  public String toString() {
    return "TestMessageContextImpl{" +
      "text='" + text + '\'' +
      '}';
  }
}
