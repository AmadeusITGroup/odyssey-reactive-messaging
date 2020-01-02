package com.amadeus.middleware.odyssey.reactive.messaging.core.topology;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Supplier;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amadeus.middleware.odyssey.reactive.messaging.core.After;
import com.amadeus.middleware.odyssey.reactive.messaging.core.Before;
import com.amadeus.middleware.odyssey.reactive.messaging.core.FunctionInvoker;
import com.amadeus.middleware.odyssey.reactive.messaging.core.NodeInterceptor;

public class InstrumentedTopologyBuilderVisitor extends AbstractVisitor {
  private static final Logger logger = LoggerFactory.getLogger(InstrumentedTopologyBuilderVisitor.class);

  private final String name;
  private final Supplier<NodeInterceptor> nodeInterceptorSupplier;
  private BeanManager beanManager;

  private TopologyBuilder builder = new TopologyBuilder();

  public static Topology build(String name, Supplier<NodeInterceptor> nodeInterceptorSupplier, BeanManager beanManager,
      Topology topology) {
    for (Node node : topology.getNodes()) {
      if (node instanceof PublisherNode) {
        InstrumentedTopologyBuilderVisitor instrumentedTopologyBuilderVisitor = new InstrumentedTopologyBuilderVisitor(
            name, nodeInterceptorSupplier, beanManager);
        node.accept(instrumentedTopologyBuilderVisitor);
        // TODO: For now, just peek the first line and return
        return instrumentedTopologyBuilderVisitor.build();
      }
    }
    return null;
  }

  public InstrumentedTopologyBuilderVisitor(String name, Supplier<NodeInterceptor> nodeInterceptorSupplier,
      BeanManager beanManager) {
    this.name = name;
    this.nodeInterceptorSupplier = nodeInterceptorSupplier;
    this.beanManager = beanManager;
  }

  @Override
  public void visit(Node node) {
    if (node instanceof PublisherNode) {
      build((PublisherNode<?>) node);
    } else if (node instanceof ProcessorNode) {
      build((ProcessorNode) node);
    } else if (node instanceof SubscriberNode) {
      build((SubscriberNode<?>) node);
    }
  }

  private void build(PublisherNode<?> node) {
    String[] children = node.getChildren()
        .keySet()
        .toArray(new String[] {});
    String[] intercepted = Arrays.stream(children)
        .map(n -> name + "." + node.getName() + "." + n)
        .toArray(String[]::new);

    PublisherNode<?> newPublisherNode = new PublisherNode<>(node.getName(), node.getPublisherInvoker(), intercepted);

    NodeInterceptor nodeInterceptor = nodeInterceptorSupplier.get();
    nodeInterceptor.setNodeName(node.getName());
    injectFields(nodeInterceptor);

    Method method = findAfterMethod(nodeInterceptor.getClass());
    if (method == null) {
      logger.warn("No after method found in {}", nodeInterceptor.getClass());
      builder.addPublisherNode(node);
      return;
    }

    FunctionInvoker functionInvoker = FunctionInvoker.builder()
        .defaultTargetInstance(nodeInterceptor)
        .targetClass(nodeInterceptor.getClass())
        .targetMethod(method)
        .build();

    ProcessorNode processorNode = new ProcessorNode("post-" + node.getName(), functionInvoker, intercepted, children);

    builder.addPublisherNode(newPublisherNode);
    builder.addProcessorNode(processorNode);
  }

  private void build(ProcessorNode node) {
    String[] parent = node.getParents()
        .keySet()
        .toArray(new String[] {});
    String[] interceptedParent = Arrays.stream(parent)
        .map(n -> name + "." + node.getName() + "." + n)
        .toArray(String[]::new);

    String[] children = node.getChildren()
        .keySet()
        .toArray(new String[] {});
    String[] interceptedChildren = Arrays.stream(children)
        .map(n -> name + "." + node.getName() + "." + n)
        .toArray(String[]::new);

    NodeInterceptor nodeInterceptor = nodeInterceptorSupplier.get();
    nodeInterceptor.setNodeName(node.getName());
    injectFields(nodeInterceptor);

    Method beforeMethod = findBeforeMethod(nodeInterceptor.getClass());
    if (beforeMethod == null) {
      interceptedParent = parent;
      logger.warn("No before method found in {}", nodeInterceptor.getClass());
    }
    Method afterMethod = findAfterMethod(nodeInterceptor.getClass());
    if (afterMethod == null) {
      interceptedChildren = children;
      logger.warn("No after method found in {}", nodeInterceptor.getClass());
    }
    if ((beforeMethod == null) && (afterMethod == null)) {
      builder.addProcessorNode(node);
      return;
    }

    if (beforeMethod != null) {
      FunctionInvoker functionInvoker = FunctionInvoker.builder()
          .defaultTargetInstance(nodeInterceptor)
          .targetClass(nodeInterceptor.getClass())
          .targetMethod(beforeMethod)
          .build();
      ProcessorNode beforeProcessorNode = new ProcessorNode("pre-" + node.getName(), functionInvoker, parent,
          interceptedParent);
      builder.addProcessorNode(beforeProcessorNode);
    }

    if (afterMethod != null) {
      FunctionInvoker functionInvoker = FunctionInvoker.builder()
          .defaultTargetInstance(nodeInterceptor)
          .targetClass(nodeInterceptor.getClass())
          .targetMethod(afterMethod)
          .build();
      ProcessorNode afterProcessorNode = new ProcessorNode("post-" + node.getName(), functionInvoker,
          interceptedChildren, children);
      builder.addProcessorNode(afterProcessorNode);
    }

    ProcessorNode processorNode = new ProcessorNode(node.getName(), node.getFunctionInvoker(), interceptedParent,
        interceptedChildren);
    builder.addProcessorNode(processorNode);
  }

  @SuppressWarnings("unchecked")
  private void build(SubscriberNode<?> node) {
    String[] parent = node.getParents()
        .keySet()
        .toArray(new String[] {});
    String[] intercepted = Arrays.stream(parent)
        .map(n -> name + "." + node.getName() + "." + n)
        .toArray(String[]::new);

    SubscriberNode<?> newSubscriberNode = new SubscriberNode(node.getName(), node.getSubscriber(), intercepted);

    NodeInterceptor nodeInterceptor = nodeInterceptorSupplier.get();
    nodeInterceptor.setNodeName(node.getName());
    injectFields(nodeInterceptor);

    Method method = findBeforeMethod(nodeInterceptor.getClass());
    if (method == null) {
      logger.warn("No before method found in {}", nodeInterceptor.getClass());
      builder.addSubscriberNode(node);
      return;
    }

    FunctionInvoker functionInvoker = FunctionInvoker.builder()
        .defaultTargetInstance(nodeInterceptor)
        .targetClass(nodeInterceptor.getClass())
        .targetMethod(method)
        .build();

    ProcessorNode processorNode = new ProcessorNode("pre-" + node.getName(), functionInvoker, intercepted, parent);

    builder.addSubscriberNode(newSubscriberNode);
    builder.addProcessorNode(processorNode);
  }

  private static Method findAfterMethod(Class<? extends NodeInterceptor> clazz) {
    for (Method m : clazz.getMethods()) {
      if (m.getAnnotation(After.class) != null) {
        return m;
      }
    }
    return null;
  }

  private static Method findBeforeMethod(Class<? extends NodeInterceptor> clazz) {
    for (Method m : clazz.getMethods()) {
      if (m.getAnnotation(Before.class) != null) {
        return m;
      }
    }
    return null;
  }

  // Method taken from delta-spike:
  // https://github.com/apache/deltaspike/blob/master/deltaspike/core/api/src/main/java/org/apache/deltaspike/core/api/provider/BeanProvider.java#L448
  /**
   * Performs dependency injection on an instance. Useful for instances which aren't managed by CDI.
   * <p/>
   * <b>Attention:</b><br/>
   * The resulting instance isn't managed by CDI; only fields annotated with @Inject get initialized.
   *
   * @param instance
   *          current instance
   * @param <T>
   *          current type
   * @return instance with injected fields (if possible - or null if the given instance is null)
   */
  @SuppressWarnings("unchecked")
  public <T> T injectFields(T instance) {
    if (instance == null) {
      return null;
    }

    CreationalContext<T> creationalContext = beanManager.createCreationalContext(null);

    AnnotatedType<T> annotatedType = beanManager.createAnnotatedType((Class<T>) instance.getClass());
    InjectionTarget<T> injectionTarget = beanManager.createInjectionTarget(annotatedType);
    injectionTarget.inject(instance, creationalContext);
    return instance;
  }

  public Topology build() {
    return builder.build(new Topology());
  }
}
