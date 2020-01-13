package com.amadeus.middleware.odyssey.reactive.messaging.core.topology;

import java.util.Map;
import java.util.Optional;

public interface Node extends Cloneable {
  String getName();

  void setName(String name);

  Map<String, Optional<Node>> getParents();

  void setParents(Map<String, Optional<Node>> parents);

  Map<String, Optional<Node>> getChildren();

  void setChildren(Map<String, Optional<Node>> children);

  void addParent(String channelName, Node parent);

  void addChildren(String channelName, Node child);

  /**
   * The visitor will be triggered for current node, then for each of the child node. This is partial implementation as
   * a tree structure of the reactive stream is assumed.
   *
   * @param visitor
   */
  void accept(Visitor visitor);
}
