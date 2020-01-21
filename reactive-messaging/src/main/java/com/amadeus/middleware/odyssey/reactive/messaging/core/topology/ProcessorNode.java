package com.amadeus.middleware.odyssey.reactive.messaging.core.topology;

import java.util.Arrays;
import java.util.Optional;

import com.amadeus.middleware.odyssey.reactive.messaging.core.FunctionInvoker;

public class ProcessorNode extends AbstractNode {
  private FunctionInvoker functionInvoker;

  public ProcessorNode() {
  }

  public ProcessorNode(ProcessorNode that) {
    this(that.name, that.functionInvoker, that.parents.keySet()
        .toArray(new String[] {}),
        that.children.keySet()
            .toArray(new String[] {}));
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
}
