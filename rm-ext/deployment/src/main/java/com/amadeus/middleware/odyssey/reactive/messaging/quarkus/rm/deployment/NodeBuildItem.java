package com.amadeus.middleware.odyssey.reactive.messaging.quarkus.rm.deployment;

import org.jboss.jandex.MethodInfo;

import io.quarkus.arc.processor.BeanInfo;
import io.quarkus.builder.item.MultiBuildItem;

public final class NodeBuildItem extends MultiBuildItem {

  private final BeanInfo bean;

  private final MethodInfo method;

  public NodeBuildItem(BeanInfo bean, MethodInfo method) {
    this.bean = bean;
    this.method = method;
  }

  public BeanInfo getBean() {
    return bean;
  }

  public MethodInfo getMethod() {
    return method;
  }
}
