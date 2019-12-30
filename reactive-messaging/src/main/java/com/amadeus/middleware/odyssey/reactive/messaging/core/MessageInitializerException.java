package com.amadeus.middleware.odyssey.reactive.messaging.core;

public class MessageInitializerException extends RuntimeException {
  private static final long serialVersionUID = 8252414597964028988L;

  public MessageInitializerException(Exception e) {
    super(e);
  }
}
