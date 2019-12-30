package com.amadeus.middleware.odyssey.reactive.messaging.core.topology;

import java.util.Map;

public interface Node {
  String getName();

  Map<String, Node> getParents();

  Map<String, Node> getChildren();

  void addParent(String channelName, Node parent);

  void addChildren(String channelName, Node child);

  /**
   * The visitor will be triggered for current node, then for each of the child node.
   *
   * This is partial implementation as a tree structure of the reactive stream is assumed.
   *
   * @param visitor
   */
  void accept(Visitor visitor);
}
