package com.amadeus.middleware.odyssey.reactive.messaging.core.cdiextension;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.FunctionInvocationException;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.MessageInitializerRegistry;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.ReflectiveFunctionInvoker;

public class CDIMessageInitializerRegistry implements MessageInitializerRegistry {
  private static final Logger logger = LoggerFactory.getLogger(CDIMessageInitializerRegistry.class);

  private static class InvokationTarget {
    private ReflectiveFunctionInvoker functionInvoker;

    public InvokationTarget(Class<?> clazz, Method method) {
      functionInvoker = new CDIFunctionInvoker(clazz, method);
    }

    public void invoke(Message message) throws FunctionInvocationException {
      functionInvoker.invoke(message);
    }

    public Class<?> getFactoryClass() {
      return functionInvoker.getTargetClass();
    }

    public Method getMethod() {
      return functionInvoker.getMethod();
    }
  }

  private List<InvokationTarget> invokationTargets = new ArrayList<>();

  public void initialize() {
    logger.debug("initialize");
    for (InvokationTarget invokationTarget : invokationTargets) {
      invokationTarget.functionInvoker.initialize();
    }
  }

  public void add(Class<?> factoryClass, Method builder) {
    invokationTargets.add(new InvokationTarget(factoryClass, builder));
  }

  @Override
  public void initialize(Message message) throws FunctionInvocationException {
    for (InvokationTarget initalizer : invokationTargets) {
      try {
        initalizer.invoke(message);
      } catch (Exception e) {
        logger.error("Exception in the call of {}.{}", initalizer.getFactoryClass()
            .getSimpleName(),
            initalizer.getMethod()
                .getName());
        throw e;
      }
    }
  }
}
