package com.amadeus.middleware.odyssey.reactive.messaging.core.cdiextension;

import javax.inject.Qualifier;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/*
 * See: AsyncProducer.producerForProgrammaticLookup for an explanation of why this ("private") annotation is introduced.
 */
@Qualifier
@Retention(RUNTIME)
public @interface TypeAnnotation {
  String value();
}
