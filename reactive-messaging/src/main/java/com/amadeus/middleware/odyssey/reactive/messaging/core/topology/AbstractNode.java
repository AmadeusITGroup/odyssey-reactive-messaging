package com.amadeus.middleware.odyssey.reactive.messaging.core.topology;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractNode implements Node {
  protected String name;
  protected Map<String, Optional<Node>> parents = new HashMap<>();
  protected Map<String, Optional<Node>> children = new HashMap<>();

  AbstractNode() {
  }

  public AbstractNode(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public Map<String, Optional<Node>> getParents() {
    return parents;
  }

  @Override
  public void setParents(Map<String, Optional<Node>> parents) {
    this.parents = parents;
  }

  @Override
  public Map<String, Optional<Node>> getChildren() {
    return children;
  }

  @Override
  public void setChildren(Map<String, Optional<Node>> children) {
    this.children = children;
  }

  @Override
  public void addParent(String channelName, Node parent) {
    parents.put(channelName, Optional.of(parent));
  }

  @Override
  public void addChildren(String channelName, Node child) {
    children.put(channelName, Optional.of(child));
  }

  @Override
  public void accept(Visitor visitor) {
    visitor.visit(this);
    visitor.beforeChildren();
    children.values()
        .forEach(child -> child.get()
            .accept(visitor));
    visitor.afterChildren();
  }

  @Override
  public String toString() {
    return "AbstractNode{" + "name='" + name + '\'' + ", parents=" + parents + ", children=" + children + '}';
  }
}
