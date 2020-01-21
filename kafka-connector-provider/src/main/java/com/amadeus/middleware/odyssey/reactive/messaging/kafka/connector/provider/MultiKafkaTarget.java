package com.amadeus.middleware.odyssey.reactive.messaging.kafka.connector.provider;

import java.util.List;

import com.amadeus.middleware.odyssey.reactive.messaging.core.MutableMetadata;

public interface MultiKafkaTarget extends MutableMetadata {
  String META_KEY = "MY_MULTIKAFKATARGET";
  String META_MERGE_KEY = KafkaTarget.META_MERGE_KEY;

  List<KafkaTarget> getTargets();
}
