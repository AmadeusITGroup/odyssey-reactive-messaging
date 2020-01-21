package com.amadeus.middleware.odyssey.reactive.messaging;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.profile.StackProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.cdi.CDIMessageBuilderImpl;

public class OnlyReactiveStreams {
  private static final Logger logger = LoggerFactory.getLogger(OnlyReactiveStreams.class);

  @State(Scope.Thread)
  public static class MyState {
    private PublisherBuilder<Integer> stream;
    private Method method;
    private int counter = 0;

    public MyState() {
      try {
        method = OnlyReactiveStreams.class.getMethod("invoked", MyState.class, int.class, Blackhole.class);
      } catch (NoSuchMethodException e) {
        logger.error("", e);
      }
    }

    @Setup(Level.Invocation)
    public void streamReinit() {
      stream = ReactiveStreams.iterate(0, t -> t + 1)
          .takeWhile(t -> t < 1_000_000);
      counter = 0;
    }
  }

  public static int invoked(MyState state, int i, Blackhole blackhole) {
    Message<String> msg = new CDIMessageBuilderImpl<String>().payload("")
        .dependencyInjection(false)
        .build();
    blackhole.consume(msg);
    state.counter = i;
    return state.counter;
  }

  @Benchmark
  public void directCall(MyState state, Blackhole blackhole) {
    state.stream.forEach(t -> invoked(state, t, blackhole))
        .run();
  }

  @Benchmark
  public void reflectiveCall(MyState state, Blackhole blackhole) {
    state.stream.forEach(t -> {
      try {
        state.method.invoke(null, state, t, blackhole);
      } catch (IllegalAccessException | InvocationTargetException e) {
        logger.error("", e);
      }
    })
        .run();
  }

  public static void main(String[] args) throws RunnerException {

    Options opt = new OptionsBuilder().include(OnlyReactiveStreams.class.getSimpleName())
        .forks(1)
        .addProfiler(StackProfiler.class)
        .build();

    new Runner(opt).run();
  }
}
