package com.amadeus.middleware.odyssey.reactive.messaging.kafka.connector.provider;

import java.util.List;

import com.amadeus.middleware.odyssey.reactive.messaging.core.MutableMetadata;

public interface MultiKafkaTarget extends MutableMetadata {
  String KEY = "MY_MULTIKAFKATARGET";
  String MERGE_KEY = KafkaTarget.MERGE_KEY;

  List<KafkaTarget> getTargets();
}
