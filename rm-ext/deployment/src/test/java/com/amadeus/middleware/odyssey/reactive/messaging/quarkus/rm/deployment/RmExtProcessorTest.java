package com.amadeus.middleware.odyssey.reactive.messaging.quarkus.rm.deployment;

import io.quarkus.test.QuarkusDevModeTest;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

class RmExtProcessorTest {

  @RegisterExtension
  static QuarkusDevModeTest test = new QuarkusDevModeTest().setArchiveProducer(
          () -> ShrinkWrap.create(JavaArchive.class).addClass(RmExtProcessor.class));

  @Test
  public void testRootResource() {
    // RestAssured.when().get("/rs/titi").then().body(Matchers.is("Root Resource"));
  }
}
