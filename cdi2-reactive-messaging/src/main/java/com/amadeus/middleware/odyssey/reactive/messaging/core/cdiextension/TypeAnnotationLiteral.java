package com.amadeus.middleware.odyssey.reactive.messaging.core.cdiextension;

import javax.enterprise.util.AnnotationLiteral;

public class TypeAnnotationLiteral extends AnnotationLiteral<TypeAnnotation> implements TypeAnnotation {
  private String theClass;

  public TypeAnnotationLiteral(String theClass) {
    this.theClass = theClass;
  }

  @Override
  public String value() {
    return theClass;
  }
}
