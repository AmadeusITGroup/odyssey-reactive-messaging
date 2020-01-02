package com.amadeus.middleware.odyssey.reactive.messaging.core.impl;

public class FunctionInvocationException extends Exception {
  private static final long serialVersionUID = -4843952416773546338L;

  public FunctionInvocationException(String msg) {
    super(msg);
  }

  public FunctionInvocationException(Exception e) {
    super(e);
  }
}
