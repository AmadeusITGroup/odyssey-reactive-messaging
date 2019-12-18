package com.amadeus.middleware.odyssey.reactive.messaging.core;

public class MessageContextProducerException extends RuntimeException {
  private static final long serialVersionUID = 8252414597964028988L;

  public MessageContextProducerException(Exception e) {
    super(e);
  }
}
