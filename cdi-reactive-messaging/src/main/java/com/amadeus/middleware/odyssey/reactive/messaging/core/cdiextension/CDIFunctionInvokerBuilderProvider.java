package com.amadeus.middleware.odyssey.reactive.messaging.core.cdiextension;

import com.amadeus.middleware.odyssey.reactive.messaging.core.FunctionInvokerBuilder;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.FunctionInvokerBuilderProvider;

public class CDIFunctionInvokerBuilderProvider implements FunctionInvokerBuilderProvider {
  @Override
  public FunctionInvokerBuilder build() {
    return new CDIFunctionInvokerBuilder();
  }
}
