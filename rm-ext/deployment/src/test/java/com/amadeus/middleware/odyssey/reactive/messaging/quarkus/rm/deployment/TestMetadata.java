package com.amadeus.middleware.odyssey.reactive.messaging.quarkus.rm.deployment;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Metadata;
import com.amadeus.middleware.odyssey.reactive.messaging.core.MessageScoped;

@MessageScoped
public interface TestMetadata extends Metadata {
  String getText();
}
