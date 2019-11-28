package com.amadeus.middleware.odyssey.reactive.messaging.core.cdiextension;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class AnnotationUtils {

  /**
   * Limitation: This has not a clean semantic as it will check for inherited annotations through interfaces... However,
   * it enables to veto Beans in order to setup our Producer.
   */
  public static <A extends Annotation> boolean hasAnnotation(Class<?> theClass, Class<A> annotationClass) {
    // TODO: Align the implementation with CDI bean lookup algorithm (or check whether it is...)
    if (theClass.getAnnotationsByType(annotationClass).length != 0) {
      return true;
    }

    Set<Class<?>> interfaces = getInterfaceClosure(theClass);
    for (Class<?> intf : interfaces) {
      if (intf.getDeclaredAnnotationsByType(annotationClass).length != 0) {
        return true;
      }
    }

    return false;
  }

  // These 2 below methods comes from:
  // https://github.com/weld/core/blob/2cabfac411d0005fcb5616d89c03596271a4c66a/impl/src/main/java/org/jboss/weld/util/reflection/Reflections.java#L468
  // Apache License: https://github.com/weld/core/blob/2cabfac411d0005fcb5616d89c03596271a4c66a/LICENSE
  public static Set<Class<?>> getInterfaceClosure(Class<?> clazz) {
    Set<Class<?>> result = new HashSet<>();
    for (Class<?> classToDiscover = clazz; classToDiscover != null; classToDiscover = classToDiscover.getSuperclass()) {
      addInterfaces(classToDiscover, result);
    }
    return result;
  }

  private static void addInterfaces(Class<?> clazz, Set<Class<?>> result) {
    Class<?>[] interfaces = clazz.getInterfaces();
    if (interfaces.length == 0) {
      return;
    }
    Collections.addAll(result, interfaces);
    for (Class<?> interfac : interfaces) {
      addInterfaces(interfac, result);
    }
  }
}
