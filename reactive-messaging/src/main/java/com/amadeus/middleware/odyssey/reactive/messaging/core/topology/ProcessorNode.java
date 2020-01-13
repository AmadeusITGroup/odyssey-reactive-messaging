package com.amadeus.middleware.odyssey.reactive.messaging.core.topology;

import java.util.Arrays;
import java.util.Optional;

import com.amadeus.middleware.odyssey.reactive.messaging.core.FunctionInvoker;

public class ProcessorNode extends AbstractNode {
  private FunctionInvoker functionInvoker;

  public ProcessorNode() {
  }

  public ProcessorNode(String name, FunctionInvoker functionInvoker, String[] inputChannels, String[] outputChannels) {
    super(name);
    this.functionInvoker = functionInvoker;
    if (inputChannels != null) {
      Arrays.stream(inputChannels)
          .forEach(channelName -> this.parents.put(channelName, Optional.empty()));
    }
    if (outputChannels != null) {
      Arrays.stream(outputChannels)
          .forEach(channelName -> this.children.put(channelName, Optional.empty()));
    }
  }

  public void setFunctionInvoker(FunctionInvoker functionInvoker) {
    this.functionInvoker = functionInvoker;
  }

  public FunctionInvoker getFunctionInvoker() {
    return functionInvoker;
  }

  @Override
  protected Object clone() {
    String[] inputChannels = parents.keySet()
        .toArray(new String[] {});
    String[] outputChannels = children.keySet()
        .toArray(new String[] {});
    return new ProcessorNode(name, functionInvoker, inputChannels, outputChannels);
  }
}
