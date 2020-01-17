package com.amadeus.middleware.odyssey.reactive.messaging.core.cdiextension;

import java.lang.reflect.Method;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;

import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.ReflectiveFunctionInvoker;

public class CDIFunctionInvoker extends ReflectiveFunctionInvoker {

  public CDIFunctionInvoker(Class<?> targetClass, Method targetMethod) {
    super(targetClass, targetMethod, true);
  }

  @Override
  public void initialize() {
    Instance<Object> instance = CDI.current()
        .getBeanManager()
        .createInstance();
    setTargetInstance(instance.select(getTargetClass())
        .get());
  }
}
