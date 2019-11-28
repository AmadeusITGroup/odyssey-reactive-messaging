package com.amadeus.middleware.odyssey.reactive.messaging.core.topology;

import java.util.Map;

public interface Node {
  String getName();

  Map<String, Node> getParents();

  Map<String, Node> getChildren();

  void addParent(String channelName, Node parent);

  void addChildren(String channelName, Node child);
}
