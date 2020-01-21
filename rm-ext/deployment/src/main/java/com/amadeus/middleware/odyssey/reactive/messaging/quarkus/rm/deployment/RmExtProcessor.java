package com.amadeus.middleware.odyssey.reactive.messaging.quarkus.rm.deployment;

import static io.quarkus.deployment.annotations.ExecutionTime.STATIC_INIT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.collections.api.multimap.set.MutableSetMultimap;
import org.eclipse.collections.impl.multimap.set.UnifiedSetMultimap;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.ParameterizedType;
import org.jboss.jandex.Type;
import org.jboss.logging.Logger;

import com.amadeus.middleware.odyssey.reactive.messaging.core.FunctionInvoker;
import com.amadeus.middleware.odyssey.reactive.messaging.core.Invoker;
import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;
import com.amadeus.middleware.odyssey.reactive.messaging.core.Metadata;
import com.amadeus.middleware.odyssey.reactive.messaging.core.topology.Node;
import com.amadeus.middleware.odyssey.reactive.messaging.core.topology.ProcessorNode;
import com.amadeus.middleware.odyssey.reactive.messaging.core.topology.PublisherNode;
import com.amadeus.middleware.odyssey.reactive.messaging.core.topology.Topology;
import com.amadeus.middleware.odyssey.reactive.messaging.core.topology.TopologyBuilder;
import com.amadeus.middleware.odyssey.reactive.messaging.quarkus.rm.AsyncBeanCreator;
import com.amadeus.middleware.odyssey.reactive.messaging.quarkus.rm.MessageInitializerRecorder;
import com.amadeus.middleware.odyssey.reactive.messaging.quarkus.rm.MessageScopedBeanCreator;
import com.amadeus.middleware.odyssey.reactive.messaging.quarkus.rm.QuarkusFunctionInvoker;
import com.amadeus.middleware.odyssey.reactive.messaging.quarkus.rm.QuarkusPublisherInvoker;
import com.amadeus.middleware.odyssey.reactive.messaging.quarkus.rm.TopologyInitializerRecorder;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.BeanArchiveIndexBuildItem;
import io.quarkus.arc.deployment.BeanContainerBuildItem;
import io.quarkus.arc.deployment.BeanRegistrarBuildItem;
import io.quarkus.arc.deployment.ValidationPhaseBuildItem;
import io.quarkus.arc.processor.AnnotationStore;
import io.quarkus.arc.processor.BeanInfo;
import io.quarkus.arc.processor.BeanRegistrar;
import io.quarkus.arc.processor.BuildExtension;
import io.quarkus.arc.processor.InjectionPointInfo;
import io.quarkus.deployment.GeneratedClassGizmoAdaptor;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.GeneratedClassBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageProxyDefinitionBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.recording.RecorderContext;
import io.quarkus.gizmo.ClassOutput;

public class RmExtProcessor {
  private static final Logger logger = Logger.getLogger(RmExtProcessor.class);

  private static final String FEATURE = "rm-ext";

  @BuildStep
  public FeatureBuildItem feature() {
    return new FeatureBuildItem(FEATURE);
  }

  @BuildStep
  NativeImageProxyDefinitionBuildItem registerProxies() {
    return new NativeImageProxyDefinitionBuildItem(Message.class.getName());
  }

  @BuildStep
  void setUnremovableBeans(BuildProducer<AdditionalBeanBuildItem> additionalBeans) {
    additionalBeans.produce(AdditionalBeanBuildItem.unremovableOf(Topology.class));
  }

  @BuildStep
  BeanRegistrarBuildItem syntheticBean(List<MetadataBuildItem> messageContextBuildItems) {
    return new BeanRegistrarBuildItem(registrationContext -> {
      registerMessageSyntheticTypes(registrationContext);
      registerMetadataSyntheticTypes(registrationContext, messageContextBuildItems);
      registerAsyncSyntheticTypes(registrationContext);
    });
  }

  void registerMessageSyntheticTypes(BeanRegistrar.RegistrationContext registrationContext) {
    Set<Type> messageTypes = new HashSet<>();
    for (InjectionPointInfo injectionPoint : registrationContext.get(BuildExtension.Key.INJECTION_POINTS)) {
      Type type = injectionPoint.getRequiredType();
      if (type.name()
          .equals(DotNames.MESSAGE)) {
        messageTypes.add(type);
      }
    }
    Type[] mt = messageTypes.toArray(new Type[] {});
    registrationContext.configure(Message.class) // What is configure() ?
        .types(mt)
        .param(MessageScopedBeanCreator.CLASS_KEY, Message.class)
        .creator(MessageScopedBeanCreator.class)
        .done();
  }

  private void registerAsyncSyntheticTypes(BeanRegistrar.RegistrationContext registrationContext) {
    MutableSetMultimap<Type, Type> baseToFullyParameterizedTypes = UnifiedSetMultimap.newMultimap();
    for (InjectionPointInfo injectionPoint : registrationContext.get(BuildExtension.Key.INJECTION_POINTS)) {
      Type type = injectionPoint.getRequiredType();
      if (!type.name()
          .equals(DotNames.ASYNC)) {
        continue;
      }
      if (type.kind() != Type.Kind.PARAMETERIZED_TYPE) {
        logger.errorf("Cannot inject type %s", type.toString());
        continue;
      }
      ParameterizedType keyType = type.asParameterizedType();
      // if Async<X<Y>> then convert to Async<X>
      Type ptype = keyType.arguments()
          .get(0);
      if (ptype.kind() == Type.Kind.PARAMETERIZED_TYPE) {
        ptype = Type.create(ptype.name(), Type.Kind.CLASS);
        keyType = ParameterizedType.create(keyType.name(), new Type[] { ptype }, null);
      }
      baseToFullyParameterizedTypes.put(keyType, type);
    }

    for (Type key : baseToFullyParameterizedTypes.keySet()) {
      Type[] mt = baseToFullyParameterizedTypes.get(key)
          .toArray(new Type[] {});

      String className = key.asParameterizedType()
          .arguments()
          .get(0)
          .toString();

      registrationContext.configure(key.name())
          .types(mt)
          .param(AsyncBeanCreator.CLASS_NAME_KEY, className)
          .creator(AsyncBeanCreator.class)
          .done();
    }
  }

  void registerMetadataSyntheticTypes(BeanRegistrar.RegistrationContext registrationContext,
      List<MetadataBuildItem> messageContextBuildItems) {
    messageContextBuildItems.stream()
        .map(MetadataBuildItem::getClazz)
        .forEach(clazz -> registrationContext.configure(clazz)
            .types(clazz)
            .param(MessageScopedBeanCreator.CLASS_KEY, clazz)
            .creator(MessageScopedBeanCreator.class)
            .done());
  }

  @BuildStep
  public void buildTopology(ValidationPhaseBuildItem validationPhase,
      BuildProducer<NodeBuildItem> nodeBuildItemBuildProducer) {

    validationPhase.getContext()
        .beans()
        .classBeans()
        .stream()
        .forEach(beanInfo -> beanInfo.getTarget()
            .ifPresent(annotationTarget -> {
              for (MethodInfo methodInfo : annotationTarget.asClass()
                  .methods()) {
                AnnotationStore annotationStore = validationPhase.getContext()
                    .get(BuildExtension.Key.ANNOTATION_STORE);
                AnnotationInstance incoming = annotationStore.getAnnotation(methodInfo, DotNames.INCOMING);
                AnnotationInstance outgoing = annotationStore.getAnnotation(methodInfo, DotNames.OUTGOING);
                if ((incoming != null) || (outgoing != null)) {
                  logger.debugf("node %s.%s", beanInfo.toString(), methodInfo.toString());
                  nodeBuildItemBuildProducer.produce(new NodeBuildItem(beanInfo, methodInfo));
                }
              }
            }));

  }

  @BuildStep
  public void lookupMessageInitializer(ValidationPhaseBuildItem validationPhase,
      BuildProducer<MessageInitializerBuildItem> messageInitializerBuildItemBuildProducer) {
    validationPhase.getContext()
        .beans()
        .classBeans()
        .stream()
        .forEach(beanInfo -> beanInfo.getTarget()
            .map(AnnotationTarget::asClass)
            .map(ClassInfo::methods)
            .ifPresent(methods -> {
              AnnotationStore annotationStore = validationPhase.getContext()
                  .get(BuildExtension.Key.ANNOTATION_STORE);
              methods.stream()
                  .forEach(methodInfo -> {
                    if (annotationStore.hasAnnotation(methodInfo, DotNames.MESSAGE_INITIALIZER)) {
                      logger.debugf("messageinitializer %s.%s", beanInfo.toString(), methodInfo.toString());
                      messageInitializerBuildItemBuildProducer
                          .produce(new MessageInitializerBuildItem(beanInfo, methodInfo));
                    }
                  });
            }));
  }

  @BuildStep
  public void processMetadata(BeanArchiveIndexBuildItem beanArchiveIndexBuildItem,
      BuildProducer<NativeImageProxyDefinitionBuildItem> nativeImageProxyDefinitionBuildItemBuildProducer,
      BuildProducer<MetadataBuildItem> messageContextBuildItemBuildProducer) {
    final IndexView index = beanArchiveIndexBuildItem.getIndex();
    final Collection<AnnotationInstance> stereotypeInstances = index.getAnnotations(DotNames.MESSAGE_SCOPED);
    for (AnnotationInstance annotationInstance : stereotypeInstances) {
      AnnotationTarget annotationTarget = annotationInstance.target();
      if (annotationTarget.kind() != AnnotationTarget.Kind.CLASS) {
        continue;
      }
      Class<?> clazz = RmExtProcessorHelpers.load(annotationTarget.asClass()
          .name()
          .toString(),
          Thread.currentThread()
              .getContextClassLoader());
      if (Metadata.class.isAssignableFrom(clazz)) {
        logger.infof("Found declaration of Metadata " + clazz.getName());
        messageContextBuildItemBuildProducer.produce(new MetadataBuildItem(clazz));
        nativeImageProxyDefinitionBuildItemBuildProducer
            .produce(new NativeImageProxyDefinitionBuildItem(clazz.getName()));
      }
    }
  }

  private Node processFlowingMethod(TopologyBuilder builder, BeanInfo bean, MethodInfo method) {
    if (method.annotation(DotNames.INCOMING) != null) {
      return processFlowingProcessor(builder, bean, method);
    } else if (method.annotation(DotNames.OUTGOING) != null) {
      return processFlowingPublisher(builder, bean, method);
    }
    return null;
  }

  private ProcessorNode processFlowingProcessor(TopologyBuilder builder, BeanInfo beanInfo, MethodInfo methodInfo) {

    QuarkusFunctionInvoker functionInvoker = new QuarkusFunctionInvoker();
    functionInvoker.setBeanId(beanInfo.getIdentifier());
    functionInvoker.setMethodName(methodInfo.name());
    functionInvoker.setSignature(FunctionInvoker.Signature.DIRECT);

    List<String> is = new ArrayList<>();
    List<String> os = new ArrayList<>();
    for (AnnotationInstance annotationInstance : methodInfo.annotations()) {
      if (annotationInstance.name()
          .equals(DotNames.INCOMING)) {
        is.add(annotationInstance.value()
            .asString());
      } else if (annotationInstance.name()
          .equals(DotNames.OUTGOING)) {
        os.add(annotationInstance.value()
            .asString());
      }
    }
    if (logger.isDebugEnabled()) {
      logger.debugf("in[%s] out[%s] processor: %s => %s", is, os, beanInfo.getTarget()
          .map(AnnotationTarget::asClass)
          .map(ClassInfo::name)
          .map(DotName::toString)
          .orElse("unknown"), methodInfo.name());
    }
    return builder.addProcessor(getName(beanInfo, methodInfo), functionInvoker, is.toArray(new String[] {}),
        os.toArray(new String[] {}));
  }

  private PublisherNode processFlowingPublisher(TopologyBuilder builder, BeanInfo beanInfo, MethodInfo methodInfo) {
    QuarkusPublisherInvoker publisherInvoker = new QuarkusPublisherInvoker();
    publisherInvoker.setBeanId(beanInfo.getIdentifier());

    List<String> os = new ArrayList<>();
    for (AnnotationInstance annotationInstance : methodInfo.annotations()) {
      if (annotationInstance.name()
          .equals(DotNames.OUTGOING)) {
        os.add(annotationInstance.value()
            .asString());
      }
    }
    if (logger.isDebugEnabled()) {
      logger.infof("out[%s] publisher: %s.%s", os, beanInfo.getTarget()
          .map(AnnotationTarget::asClass)
          .map(ClassInfo::name)
          .map(DotName::toString)
          .orElse("unknown"), methodInfo.name());
    }
    return builder.addPublisherNode(getName(beanInfo, methodInfo), publisherInvoker, os.toArray(new String[] {}));
  }

  private String getName(BeanInfo bean, MethodInfo method) {
    AnnotationInstance nodeNameAnnotationInstance = method.annotation(DotNames.NODENAME);
    if (nodeNameAnnotationInstance != null) {
      return nodeNameAnnotationInstance.value()
          .asString();
    }
    return bean.getTarget()
        .map(target -> target.asClass()
            .name() + "." + method.name())
        .orElse(null);
  }

  @BuildStep(loadsApplicationClasses = true)
  @Record(STATIC_INIT)
  public void build(TopologyInitializerRecorder topologyInitializerRecorder,
      MessageInitializerRecorder messageInitializerRecorder, RecorderContext recorderContext,
      BuildProducer<ReflectiveClassBuildItem> reflectiveClass, BuildProducer<GeneratedClassBuildItem> generatedClass,
      List<NodeBuildItem> nodeBuildItems, List<MessageInitializerBuildItem> messageInitializerBuildItems,
      List<MetadataBuildItem> messageContextBuildItems, BeanContainerBuildItem beanContainer) {
    logger.info("build static init {");

    // This will enable QuarkusAsync to perform class loading
    for (MetadataBuildItem messageContextBuildItem : messageContextBuildItems) {
      reflectiveClass.produce(new ReflectiveClassBuildItem(false, false, messageContextBuildItem.getClazz()));
    }

    ClassOutput classOutput = new GeneratedClassGizmoAdaptor(generatedClass, true);

    initializerMessageInitializers(messageInitializerRecorder, recorderContext, reflectiveClass,
        messageInitializerBuildItems, classOutput);

    initializeTopology(topologyInitializerRecorder, recorderContext, reflectiveClass, nodeBuildItems, beanContainer,
        classOutput);

    logger.info("} build build static init");
  }

  private void initializeTopology(TopologyInitializerRecorder topologyInitializerRecorder,
      RecorderContext recorderContext, BuildProducer<ReflectiveClassBuildItem> reflectiveClass,
      List<NodeBuildItem> nodeBuildItems, BeanContainerBuildItem beanContainer, ClassOutput classOutput) {
    TopologyBuilder builder = new TopologyBuilder();
    for (NodeBuildItem nodeBuildItem : nodeBuildItems) {
      BeanInfo bean = nodeBuildItem.getBean();
      MethodInfo methodInfo = nodeBuildItem.getMethod();
      Node node = processFlowingMethod(builder, nodeBuildItem.getBean(), nodeBuildItem.getMethod());
      if (node instanceof PublisherNode) {
        processPublisherNode(recorderContext, reflectiveClass, classOutput, bean, methodInfo, (PublisherNode) node);
      } else if (node instanceof ProcessorNode) {
        processProcessorNode(recorderContext, reflectiveClass, classOutput, bean, methodInfo, (ProcessorNode) node);
      }
    }
    topologyInitializerRecorder.initialize(beanContainer.getValue(), builder);
  }

  private void initializerMessageInitializers(MessageInitializerRecorder messageInitializerRecorder,
      RecorderContext recorderContext, BuildProducer<ReflectiveClassBuildItem> reflectiveClass,
      List<MessageInitializerBuildItem> messageInitializerBuildItems, ClassOutput classOutput) {
    List<QuarkusFunctionInvoker> mii = new ArrayList<>();
    for (MessageInitializerBuildItem mib : messageInitializerBuildItems) {
      BeanInfo beanInfo = mib.getBean();
      MethodInfo methodInfo = mib.getMethod();
      QuarkusFunctionInvoker functionInvoker = new QuarkusFunctionInvoker();
      functionInvoker.setBeanId(beanInfo.getIdentifier());
      functionInvoker.setMethodName(methodInfo.name());
      functionInvoker.setSignature(FunctionInvoker.Signature.DIRECT);
      setupFunctionInvoker(recorderContext, reflectiveClass, classOutput, beanInfo, methodInfo, functionInvoker);
      mii.add(functionInvoker);
    }
    messageInitializerRecorder.initialize(mii);

  }

  @SuppressWarnings("unchecked")
  private void processProcessorNode(RecorderContext recorderContext,
      BuildProducer<ReflectiveClassBuildItem> reflectiveClass, ClassOutput classOutput, BeanInfo bean,
      MethodInfo methodInfo, ProcessorNode processorNode) {

    QuarkusFunctionInvoker functionInvoker = (QuarkusFunctionInvoker) processorNode.getFunctionInvoker();
    setupFunctionInvoker(recorderContext, reflectiveClass, classOutput, bean, methodInfo, functionInvoker);
  }

  @SuppressWarnings("unchecked")
  private void setupFunctionInvoker(RecorderContext recorderContext,
      BuildProducer<ReflectiveClassBuildItem> reflectiveClass, ClassOutput classOutput, BeanInfo bean,
      MethodInfo methodInfo, QuarkusFunctionInvoker functionInvoker) {
    String generatedInvokerName = RmExtProcessorHelpers.generateInvoker(bean, methodInfo, classOutput);
    reflectiveClass.produce(new ReflectiveClassBuildItem(false, false, generatedInvokerName));

    String className = bean.getTarget()
        .map(AnnotationTarget::asClass)
        .map(ClassInfo::name)
        .map(DotName::toString)
        .orElse(null);

    reflectiveClass.produce(new ReflectiveClassBuildItem(true, false, className));

    java.lang.reflect.Type[] parameterTypes = new java.lang.reflect.Type[methodInfo.parameters()
        .size()];
    List<java.lang.reflect.Type> asyncParameterTypes = new ArrayList<>();
    for (int i = 0; i < methodInfo.parameters()
        .size(); i++) {
      Type type = methodInfo.parameters()
          .get(i);
      parameterTypes[i] = recorderContext.classProxy(type.name()
          .toString());
      if (type.name()
          .equals(DotNames.ASYNC)) {
        asyncParameterTypes.add(recorderContext.classProxy(type.asParameterizedType()
            .arguments()
            .get(0)
            .toString()));
      }
    }
    functionInvoker.setParameterTypes(parameterTypes);
    functionInvoker.setAsyncParameterTypes(asyncParameterTypes.toArray(new java.lang.reflect.Type[] {}));
    functionInvoker.setInvokerClass((Class<? extends Invoker>) recorderContext.classProxy(generatedInvokerName));
  }

  @SuppressWarnings("unchecked")
  private void processPublisherNode(RecorderContext recorderContext,
      BuildProducer<ReflectiveClassBuildItem> reflectiveClass, ClassOutput classOutput, BeanInfo bean,
      MethodInfo methodInfo, PublisherNode publisherNode) {

    QuarkusPublisherInvoker publisherInvoker = (QuarkusPublisherInvoker) publisherNode.getPublisherInvoker();
    String generatedInvokerName = RmExtProcessorHelpers.generateInvoker(bean, methodInfo, classOutput);
    reflectiveClass.produce(new ReflectiveClassBuildItem(false, false, generatedInvokerName));

    publisherInvoker.setInvokerClass(recorderContext.classProxy(generatedInvokerName));
  }
}
