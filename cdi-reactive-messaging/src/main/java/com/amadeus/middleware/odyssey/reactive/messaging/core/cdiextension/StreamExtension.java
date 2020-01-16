package com.amadeus.middleware.odyssey.reactive.messaging.core.cdiextension;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessInjectionPoint;
import javax.enterprise.inject.spi.ProcessManagedBean;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.eclipse.collections.api.multimap.set.MutableSetMultimap;
import org.eclipse.collections.impl.multimap.set.UnifiedSetMultimap;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Async;
import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageInitializer;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageScoped;
import com.amadeus.middleware.odyssey.reactive.messaging.core.Metadata;
import com.amadeus.middleware.odyssey.reactive.messaging.core.NodeName;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.PublisherInvokerImpl;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.ReactiveMessagingContext;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.cdi.CDIAsync;
import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.cdi.ProxyProducer;
import com.amadeus.middleware.odyssey.reactive.messaging.core.topology.Topology;
import com.amadeus.middleware.odyssey.reactive.messaging.core.topology.TopologyBuilder;

public class StreamExtension implements Extension {
  private static final Logger logger = LoggerFactory.getLogger(StreamExtension.class);

  private MessageInitializerRegistryImpl messageInitializerRegistry = new MessageInitializerRegistryImpl();

  private TopologyBuilder builder = new TopologyBuilder();
  private List<AnnotatedType<? extends Metadata>> metadata = new ArrayList<>();

  private MutableSetMultimap<Type, Type> baseToFullyParameterizedTypes = UnifiedSetMultimap.newMultimap();

  @SuppressWarnings("rawtypes")
  <T extends Async> void vetoAsync(@Observes ProcessAnnotatedType<T> event) {
    event.veto();
  }

  <T extends Metadata> void registerAndVetoMessageScopedMetadatas(@Observes ProcessAnnotatedType<T> event) {
    Class<?> theClass = event.getAnnotatedType()
        .getJavaClass();
    if (!AnnotationUtils.hasAnnotation(theClass, MessageScoped.class)) {
      return;
    }
    metadata.add(event.getAnnotatedType());
    logger.trace("Vetoing: {}", theClass.getName());
    event.veto();
  }

  <T, X> void registerAllMessageAndAsyncTypes(@Observes ProcessInjectionPoint<T, X> processInjectionPoint) {
    Member member = processInjectionPoint.getInjectionPoint()
        .getMember();
    Class<?> dc = member.getDeclaringClass();
    if (Field.class.isAssignableFrom(member.getClass())) {
      registerAllMessageAndAsyncTypesInFields(dc, (Field) member);
    } else if (Method.class.isAssignableFrom(member.getClass())) {
      registerAllMessageAndAsyncTypesInMethods(dc, (Method) member);
    } else if (Constructor.class.isAssignableFrom(member.getClass())) {
      registerAllMessageAndAsyncTypesInConstructors(dc, (Constructor<?>) member);
    }
  }

  private void registerAllMessageAndAsyncTypesInFields(Class<?> dc, Field member) {
    try {
      Field field = dc.getDeclaredField(member.getName());
      if (field != null) {
        if (!field.isAccessible()) {
          field.setAccessible(true);
        }
        registerMessageAndAsyncType(field.getType(), field.getGenericType());
      }
    } catch (NoSuchFieldException e) {
      logger.error("Unable to access the field: {}.{} {}", dc.getName(), member.getName(), e);
    }
  }

  private void registerAllMessageAndAsyncTypesInConstructors(Class<?> dc, Constructor<?> constructor) {
    for (Parameter parameter : constructor.getParameters()) {
      registerMessageAndAsyncType(parameter.getType(), parameter.getParameterizedType());
    }
  }

  private void registerAllMessageAndAsyncTypesInMethods(Class<?> dc, Method method) {
    for (Parameter parameter : method.getParameters()) {
      registerMessageAndAsyncType(parameter.getType(), parameter.getParameterizedType());
    }
  }

  private void registerMessageAndAsyncType(Class<?> clazz, Type type) {
    if (!(type instanceof ParameterizedType)) {
      return;
    }
    ParameterizedType parameterizedType = (ParameterizedType) type;
    // Message -> Message<Whatever>
    if (Message.class.isAssignableFrom(clazz) || Metadata.class.isAssignableFrom(clazz)) {
      if (!(parameterizedType.getActualTypeArguments()[0] instanceof WildcardType)) {
        baseToFullyParameterizedTypes.put(clazz, type);
      }
      return;
    }
    // Async<X> -> Async<X<Whatever>>
    if (!Async.class.isAssignableFrom(clazz) || (parameterizedType.getActualTypeArguments().length != 1)) {
      return;
    }
    Type asyncTypeParameters = parameterizedType.getActualTypeArguments()[0];
    if (asyncTypeParameters instanceof ParameterizedType) {
      ParameterizedType pt = (ParameterizedType) asyncTypeParameters;
      if (!(pt.getActualTypeArguments()[0] instanceof WildcardType)) {
        baseToFullyParameterizedTypes.put(TypeUtils.parameterize(Async.class, pt.getRawType()), type);
      }
    }
  }

  @SuppressWarnings("unchecked")
  <T> void processManagedBean(@Observes ProcessManagedBean<T> event) {
    AnnotatedType<?> annotatedType = event.getAnnotatedBeanClass();
    annotatedType.getMethods()
        .stream()
        .filter(m -> m.isAnnotationPresent(Incoming.class) || m.isAnnotationPresent(Outgoing.class))
        .forEach(method -> processFlowingMethod(annotatedType, method));

    annotatedType.getMethods()
        .stream()
        .filter(m -> m.isAnnotationPresent(MessageInitializer.class))
        .forEach(annotatedMethod -> {
          messageInitializerRegistry.add(event.getAnnotatedBeanClass()
              .getJavaClass(), annotatedMethod.getJavaMember());
        });
  }

  public void processFlowingMethod(AnnotatedType<?> annotatedType, AnnotatedMethod<?> method) {
    if (method.isAnnotationPresent(Incoming.class)) {
      processFlowingProcessor(annotatedType, method);
    } else if (method.isAnnotationPresent(Outgoing.class)) {
      processFlowingPublisher(annotatedType, method);
    }
  }

  private void processFlowingProcessor(AnnotatedType<?> annotatedType, AnnotatedMethod<?> method) {
    CDIFunctionInvoker functionInvoker = new CDIFunctionInvoker(annotatedType.getJavaClass(), method.getJavaMember());
    Stream<?> is = method.getAnnotations(Incoming.class)
        .stream()
        .map(annotation -> ((Incoming) annotation).value());
    String[] inputChannels = is.collect(Collectors.toList())
        .toArray(new String[] {});
    Stream<?> os = method.getAnnotations(Outgoing.class)
        .stream()
        .map(annotation -> ((Outgoing) annotation).value());
    String[] outputChannels = os.collect(Collectors.toList())
        .toArray(new String[] {});
    builder.addProcessor(getName(annotatedType, method), functionInvoker, inputChannels, outputChannels);
  }

  private void processFlowingPublisher(AnnotatedType<?> annotatedType, AnnotatedMethod<?> method) {
    PublisherInvokerImpl<?> publisherInvoker = new PublisherInvokerImpl<>(annotatedType.getJavaClass(),
        method.getJavaMember());
    Stream<String> os = method.getAnnotations(Outgoing.class)
        .stream()
        .map(annotation -> ((Outgoing) annotation).value());
    String[] outputChannels = os.collect(Collectors.toList())
        .toArray(new String[] {});
    builder.addPublisherNode(getName(annotatedType, method), publisherInvoker, outputChannels);
  }

  private static String getName(AnnotatedType<?> annotatedType, AnnotatedMethod<?> method) {
    NodeName nodeName = method.getAnnotation(NodeName.class);
    if (nodeName != null) {
      return nodeName.value();
    }
    return annotatedType.getJavaClass()
        .getName() + "."
        + method.getJavaMember()
            .getName();
  }

  /**
   * Register the required CDI producer for all the Message, Metadata and Async types.
   * 
   * @param abd
   *          CDI event
   */
  void registerProducers(@Observes AfterBeanDiscovery abd) {
    registerMessageScopedClassProducer(abd, Message.class);
    registerMessageScopedAsyncClassProducer(abd, Message.class);

    metadata.stream()
        // Skip producer exposition for non-directly annotated classes
        .filter(annotatedType -> annotatedType.getJavaClass()
            .isAnnotationPresent(MessageScoped.class))
        .forEach(annotatedType -> registerMetadataRelatedProducers(abd, annotatedType));
  }

  /**
   * Register the CDI producers for an actual Metadata, Async&lt;Metadata&gt; and all of their used
   * parameterized versions.
   *
   * @param abd
   *          CDI event
   * @param at
   *          The Metadata actual type.
   */
  void registerMetadataRelatedProducers(AfterBeanDiscovery abd, AnnotatedType<?> at) {
    registerMessageScopedClassProducer(abd, at.getJavaClass());
    registerMessageScopedAsyncClassProducer(abd, at.getJavaClass());
  }

  /**
   * Register the CDI producer for a MessageScoped class and all of its used parameterized versions.
   *
   * @param abd
   *          CDI event
   * @param clazz
   *          The MessageScoped class.
   */
  private void registerMessageScopedClassProducer(AfterBeanDiscovery abd, Class<?> clazz) {
    Set<Type> messageScopedTypes = baseToFullyParameterizedTypes.get(clazz)
        .toSet();
    messageScopedTypes.add(clazz);
    if (logger.isTraceEnabled()) {
      messageScopedTypes.forEach(mt -> logger.trace("Registering producer for: {}", mt));
    }
    abd.<Metadata> addBean()
        .types(messageScopedTypes)
        .createWith(new ProxyProducer<>(clazz));
  }

  /**
   * Register the CDI producer for the Async&lt; <em>MessageScoped class</em> &gt; type and all of its used
   * parameterized versions.
   *
   * @param abd
   *          CDI event
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  private void registerMessageScopedAsyncClassProducer(AfterBeanDiscovery abd, Class<?> clazz) {
    ParameterizedType asyncType = TypeUtils.parameterize(Async.class, clazz);
    Set<Type> asyncMessageScopedTypes = baseToFullyParameterizedTypes.get(asyncType)
        .toSet();
    asyncMessageScopedTypes.add(asyncType);
    if (logger.isTraceEnabled()) {
      asyncMessageScopedTypes.forEach(mt -> logger.trace("Registering producer for: {}", mt));
    }
    abd.<Async> addBean()
        .types(asyncMessageScopedTypes)
        .createWith(cc -> new CDIAsync(clazz));
  }

  void afterDeploymentValidation(@Observes AfterDeploymentValidation abd, BeanManager beanManager) {
    logger.debug("AfterDeploymentValidation");

    ReactiveMessagingContext.setMessageInitializerRegistry(messageInitializerRegistry);
    messageInitializerRegistry.initialize(beanManager);

    Instance<Object> instance = beanManager.createInstance();

    // Build the topology
    Topology topology = instance.select(Topology.class)
        .get();
    builder.build(topology);
    FunctionInvokerInitializer functionInvokerInitializer = new FunctionInvokerInitializer(beanManager);
    topology.accept(functionInvokerInitializer);
    builder = null;

    logger.info("StreamExtension initialized");
  }

}
