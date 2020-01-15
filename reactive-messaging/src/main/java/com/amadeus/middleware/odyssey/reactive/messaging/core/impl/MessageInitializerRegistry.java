package com.amadeus.middleware.odyssey.reactive.messaging.core.impl;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;

public interface MessageInitializerRegistry {

  void initialize(Message message) throws FunctionInvocationException;
}
