package com.amadeus.middleware.odyssey.reactive.messaging.core.cdiextension;

import javax.inject.Inject;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Async;
import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;

public class MyProcessor {

  private BasicContext basicContext;

  @Inject
  public MyProcessor(BasicContext basicContext, ParameterizedContext pc) {
    this.basicContext = basicContext;
  }

  @Inject
  public void initializeAsync(Async<BasicContext> basicContext, Async<ParameterizedContext> pc) {
  }

  @Inject
  public void initializeAsync2(Async<ParameterizedContext<String>> pc) {
  }

  @Inject
  public void initializeAsync3(Async<ParameterizedContext<?>> pc) {
  }

  @Inject
  private Message<?> message;

  @Inject
  private Message<String> messageString;

  @Inject
  private Async<Message<?>> asyncMessage;

  @Inject
  private Async<Message<String>> asyncMessageString;

  public Message<?> getMessage() {
    return message;
  }

  public Message<String> getMessageString() {
    return messageString;
  }

  public Async<Message<?>> getAsyncMessage() {
    return asyncMessage;
  }
}
