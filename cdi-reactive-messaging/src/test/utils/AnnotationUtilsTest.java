package com.amadeus.middleware.odyssey.reactive.messaging.core.cdiextension;

import org.junit.Assert;
import org.junit.Test;

public class AnnotationUtilsTest {

  @Test
  public void testHasAnnotation() throws Exception {

    Assert.assertTrue(AnnotationUtils.hasAnnotation(Foo.class, MyAnnotation.class));
    Assert.assertTrue(AnnotationUtils.hasAnnotation(Foo.class, MyInheritedAnnotation.class));

    Assert.assertFalse(AnnotationUtils.hasAnnotation(SubFoo.class, MyAnnotation.class));
    Assert.assertTrue(AnnotationUtils.hasAnnotation(SubFoo.class, MyInheritedAnnotation.class));

    // Inherited annotation don't go through interfaces: https://docs.oracle.com/javase/8/docs/api/index.html
    // Assert.assertFalse(AnnotationUtils.hasAnnotation(SubBar.class, MyAnnotation.class));
    // Assert.assertFalse(AnnotationUtils.hasAnnotation(SubBar.class, MyInheritedAnnotation.class));
  }

}
