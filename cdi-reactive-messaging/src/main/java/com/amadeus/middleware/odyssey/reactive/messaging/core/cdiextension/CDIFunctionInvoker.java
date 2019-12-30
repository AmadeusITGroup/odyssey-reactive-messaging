package com.amadeus.middleware.odyssey.reactive.messaging.core.cdiextension;

import java.lang.reflect.Method;

import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.BaseFunctionInvoker;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.FunctionInvocationException;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.MessageImpl;

public class CDIFunctionInvoker extends BaseFunctionInvoker {
  private static final Logger logger = LoggerFactory.getLogger(CDIFunctionInvoker.class);

  public CDIFunctionInvoker(Class<?> targetClass, Method targetMethod) {
    super(targetClass, targetMethod);
  }

  public Object invoke(Object targetInstance, Message<?> message) throws FunctionInvocationException {
    MessageImpl<?> messageImpl = (MessageImpl<?>) message;
    MessageScopedContext context = MessageScopedContext.getInstance();
    context.start(messageImpl.getScopeContextId());
    try {
      return super.invoke(targetInstance, message);
    } catch (FunctionInvocationException e) {
      throw e;
    } catch (Exception e) {
      throw new FunctionInvocationException(e);
    } finally {
      context.suspend();
    }
  }
}
