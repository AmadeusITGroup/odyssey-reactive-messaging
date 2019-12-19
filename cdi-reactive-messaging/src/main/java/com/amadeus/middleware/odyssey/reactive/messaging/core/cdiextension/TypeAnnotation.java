package com.amadeus.middleware.odyssey.reactive.messaging.core.cdiextension;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

import javax.inject.Qualifier;

/*
 * See: AsyncProducer.producerForProgrammaticLookup for an explanation of why this ("private") annotation is introduced.
 */
@Qualifier
@Retention(RUNTIME)
public @interface TypeAnnotation {
  String value();
}
