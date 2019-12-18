package com.amadeus.middleware.experiment.cdi;

import javax.inject.Inject;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Async;
import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;

public class MyProcessor {

  @Inject
  private Message<?> message;

  @Inject
  private Message<String> messageString;

  @Inject
  private Async<Message<?>> asyncMessage;

  @SuppressWarnings("unused")
  @Inject
  private Async<Message<String>> asyncMessageString;

  public MyProcessor() {
  }

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
