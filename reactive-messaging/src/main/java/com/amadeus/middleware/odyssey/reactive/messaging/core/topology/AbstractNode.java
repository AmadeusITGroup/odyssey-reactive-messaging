package com.amadeus.middleware.odyssey.reactive.messaging.core.topology;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractNode implements Node {
  private String name;
  protected Map<String, Node> parents = new HashMap<>();
  protected Map<String, Node> children = new HashMap<>();

  public AbstractNode(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Map<String, Node> getParents() {
    return parents;
  }

  @Override
  public Map<String, Node> getChildren() {
    return children;
  }

  @Override
  public void addParent(String channelName, Node parent) {
    parents.put(channelName, parent);
  }

  @Override
  public void addChildren(String channelName, Node child) {
    children.put(channelName, child);
  }

  @Override
  public void accept(Visitor visitor) {
    visitor.visit(this);
    visitor.beforeChildren();
    children.values()
        .forEach(child -> child.accept(visitor));
    visitor.afterChildren();
  }
}
