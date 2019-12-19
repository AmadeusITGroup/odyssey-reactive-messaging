package com.amadeus.middleware.odyssey.reactive.messaging;

import java.lang.reflect.Method;

import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;

import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.profile.StackProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;
import com.amadeus.middleware.odyssey.reactive.messaging.core.cdiextension.CDIMessageBuilderImpl;
import com.amadeus.middleware.odyssey.reactive.messaging.core.cdiextension.MessageScopedContext;
import com.amadeus.middleware.odyssey.reactive.messaging.core.cdiextension.StreamExtension;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.MessageImpl;

public class CDIReactiveStreams {
  private static final Logger logger = LoggerFactory.getLogger(CDIReactiveStreams.class);

  public static class DummyClass {
  }

  @State(Scope.Thread)
  public static class MyState {
    public PublisherBuilder<Integer> stream;
    public Method method;
    public SeContainer container;
    public int counter = 0;

    public MyState() {
      try {
        method = CDIReactiveStreams.class.getMethod("invoked", MyState.class, int.class, Blackhole.class);
      } catch (NoSuchMethodException e) {
        logger.error("", e);
      }
    }

    @SuppressWarnings("unchecked")
    @Setup(Level.Iteration)
    public void setup() {
      container = SeContainerInitializer.newInstance()
          .disableDiscovery()
          .addPackages(true, com.amadeus.middleware.odyssey.reactive.messaging.core.Message.class)
          .addExtensions(StreamExtension.class)
          .addBeanClasses(CDIReactiveStreams.DummyClass.class)
          .initialize();
    }

    @TearDown(Level.Iteration)
    public void tearDown() {
      container.close();
    }

    @Setup(Level.Invocation)
    public void streamReinit() {
      stream = ReactiveStreams.iterate(0, t -> t + 1)
          .takeWhile(t -> t < 1_000_000);
      counter = 0;
    }
  }

  public static int invoked(MyState state, int i, Blackhole blackhole) {
    Message<String> msg = null;
    try {
      msg = new CDIMessageBuilderImpl<String>().payload("")
          .build();
      MessageImpl<String> msgImpl = (MessageImpl<String>) msg;
      MessageScopedContext.getInstance()
          .start(msgImpl.getScopeContextId());

      /*
       * Instance<Message> messageInstance = state.container.getBeanManager() .createInstance() .select(Message.class);
       * messageInstance.get() .getPayload();
       */
      state.counter = i;
      blackhole.consume(state.counter);

    } catch (Exception e) {
      logger.error("", e);
    } finally {
      if (MessageScopedContext.getInstance()
          .isActive()) {
        MessageScopedContext.getInstance()
            .suspend();
      }
      if (msg != null) {
        MessageScopedContext.getInstance()
            .destroy(((MessageImpl<String>) msg).getScopeContextId());
      }
    }
    return state.counter;
  }

  @Benchmark
  public void directCall(MyState state, Blackhole blackhole) {
    state.stream.forEach(t -> invoked(state, t, blackhole))
        .run();
  }

  public static void main(String[] args) throws RunnerException {

    Options opt = new OptionsBuilder().include(CDIReactiveStreams.class.getSimpleName())
        .forks(1)
        .addProfiler(StackProfiler.class)
        .build();

    new Runner(opt).run();
  }
}
