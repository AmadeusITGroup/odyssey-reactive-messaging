package com.amadeus.middleware.odyssey.reactive.messaging.quarkus.rm;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Invoker;

public class MessageInitializerDescription {
  private String beanId;
  private Class<? extends Invoker> invokerClass;

  public MessageInitializerDescription() {
  }
  public MessageInitializerDescription(String beanId, Class<? extends Invoker> invokerClass) {
    this.beanId = beanId;
    this.invokerClass = invokerClass;
  }

  public void setBeanId(String beanId) {
    this.beanId = beanId;
  }

  public String getBeanId() {
    return beanId;
  }

  public void setInvokerClass(Class<? extends Invoker> invokerClass) {
    this.invokerClass = invokerClass;
  }

  public Class<? extends Invoker> getInvokerClass() {
    return invokerClass;
  }
}
