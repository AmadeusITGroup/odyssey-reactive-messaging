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

  public String getName() {
    return name;
  }

  public Map<String, Node> getParents() {
    return parents;
  }

  public Map<String, Node> getChildren() {
    return children;
  }

  public void addParent(String channelName, Node parent) {
    parents.put(channelName, parent);
  }

  public void addChildren(String channelName, Node child) {
    children.put(channelName, child);
  }
}
