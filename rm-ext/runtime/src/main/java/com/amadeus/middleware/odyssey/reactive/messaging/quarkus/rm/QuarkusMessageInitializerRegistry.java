package com.amadeus.middleware.odyssey.reactive.messaging.quarkus.rm;

import java.util.ArrayList;
import java.util.List;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Invoker;
import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.FunctionInvocationException;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.MessageInitializerRegistry;

public class QuarkusMessageInitializerRegistry implements MessageInitializerRegistry {

  private List<QuarkusFunctionInvoker> invokers = new ArrayList<>();

  public void add(QuarkusFunctionInvoker invoker) {
    this.invokers.add(invoker);
  }

  @Override
  public void initialize(Message message) throws FunctionInvocationException {
    for(QuarkusFunctionInvoker invoker:invokers) {
      invoker.invoke(message);
    }
  }
}
