package com.amadeus.middleware.odyssey.reactive.messaging.quarkus.rm.deployment;

import java.util.List;

import javax.inject.Inject;

import org.jboss.logging.Logger;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.amadeus.middleware.odyssey.reactive.messaging.core.impl.FunctionInvocationException;
import com.amadeus.middleware.odyssey.reactive.messaging.core.reactive.ReactiveStreamFactory;
import com.amadeus.middleware.odyssey.reactive.messaging.core.topology.ProcessorNode;
import com.amadeus.middleware.odyssey.reactive.messaging.core.topology.Topology;

import io.quarkus.test.QuarkusUnitTest;

public class RmExtProcessorTest {
  private static final Logger logger = Logger.getLogger(RmExtProcessorTest.class);

  @RegisterExtension
  static final QuarkusUnitTest test = new QuarkusUnitTest()
      .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
          .addClasses(RmExtProcessor.class, DotNames.class, Processors.class, TestMetadata.class,
              TestMetadataImpl.class, MyMessageInitializer.class));

  @Inject
  public Processors processors;

  @Test
  public void testRootResource() throws FunctionInvocationException {
    Assert.assertNotNull(processors);
    Topology topology = processors.getTopology();
    Assert.assertNotNull(topology);
    List<ProcessorNode> nodes = topology.getProcessorNodes();
    Assert.assertNotNull(nodes);
    Assert.assertFalse(nodes.isEmpty());

    ReactiveStreamFactory reactiveStreamFactory = processors.getReactiveStreamFactory();
    reactiveStreamFactory.build(topology);
  }
}
