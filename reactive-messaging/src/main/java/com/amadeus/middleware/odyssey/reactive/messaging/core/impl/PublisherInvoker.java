package com.amadeus.middleware.odyssey.reactive.messaging.core.impl;

import org.reactivestreams.Publisher;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;

public interface PublisherInvoker<T> {
  Publisher<Message<T>> invoke() throws FunctionInvocationException;
}
