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
  static final QuarkusUnitTest test = new QuarkusUnitTest() // new QuarkusDevModeTest()
      .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
          .addClasses(RmExtProcessor.class, DotNames.class, Processors.class, TestMessageContext.class,
              TestMessageContextImpl.class));

  @Inject
  public Processors foo;

  @Test
  public void testRootResource() throws FunctionInvocationException {
    Assert.assertNotNull(foo);
    Topology topology = foo.getTopology();
    Assert.assertNotNull(topology);
    List<ProcessorNode> nodes = topology.getProcessorNodes();
    Assert.assertNotNull(nodes);
    Assert.assertFalse(nodes.isEmpty());

    ReactiveStreamFactory reactiveStreamFactory = foo.getReactiveStreamFactory();
    reactiveStreamFactory.build(topology);
  }
}
