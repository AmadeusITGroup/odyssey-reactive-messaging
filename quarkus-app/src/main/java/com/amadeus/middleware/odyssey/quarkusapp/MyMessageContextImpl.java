package com.amadeus.middleware.odyssey.quarkusapp;

import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageContext;

public class MyMessageContextImpl implements MyMessageContext {
  private String text;

  public MyMessageContextImpl(String text) {
    this.text = text;
  }

  @Override
  public String getText() {
    return text;
  }

  @Override
  public String getContextKey() {
    return "MMC";
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
    return "MyMessageContextImpl{" + "text='" + text + '\'' + '}';
  }
}
