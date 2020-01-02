package com.amadeus.middleware.odyssey.reactive.messaging.core.cdiextension;

import com.amadeus.middleware.odyssey.reactive.messaging.core.FunctionInvoker;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.AbstractFunctionInvokerBuilder;

public class CDIFunctionInvokerBuilder extends AbstractFunctionInvokerBuilder {
  @Override
  public FunctionInvoker build() {
    return new CDIFunctionInvoker(defaultTargetInstance, targetClass, targetMethod);
  }
}
