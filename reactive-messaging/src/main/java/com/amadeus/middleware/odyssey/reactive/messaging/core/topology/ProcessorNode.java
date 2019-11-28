package com.amadeus.middleware.odyssey.reactive.messaging.core.topology;

import java.util.Arrays;

import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.FunctionInvoker;

public class ProcessorNode extends AbstractNode {
  private FunctionInvoker functionInvoker;

  public ProcessorNode(String name, FunctionInvoker functionInvoker, String[] inputChannels, String[] outputChannels) {
    super(name);
    this.functionInvoker = functionInvoker;
    if (inputChannels != null) {
      Arrays.stream(inputChannels)
          .forEach(channelName -> this.parents.put(channelName, null));
    }
    if (outputChannels != null) {
      Arrays.stream(outputChannels)
          .forEach(channelName -> this.children.put(channelName, null));
    }
  }

  public FunctionInvoker getFunctionInvoker() {
    return functionInvoker;
  }
}
