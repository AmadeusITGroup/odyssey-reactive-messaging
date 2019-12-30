package com.amadeus.middleware.odyssey.reactive.messaging.kafka.connector.provider;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;

import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.kafka.client.consumer.KafkaConsumer;
import io.vertx.reactivex.kafka.client.consumer.KafkaConsumerRecord;

public class KafkaRecordPublisher {
  private static final Logger logger = LoggerFactory.getLogger(KafkaRecordPublisher.class);

  private Vertx vertx = Vertx.vertx();

  @Outgoing("input_channel")
  public Publisher<Message<String>> publish() {
    Map<String, String> config = new HashMap<>();
    config.put("bootstrap.servers", "127.0.0.1:9092");
    config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    config.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    config.put("group.id", "my_group");
    config.put("auto.offset.reset", "earliest");
    config.put("enable.auto.commit", "false");

    KafkaConsumer<String, String> consumer = KafkaConsumer.create(vertx, config);
    consumer.subscribe("mytopic");

    return consumer.toFlowable()
        .doOnEach(r -> logger.debug("producing {}", r.getValue()
            .key()))
        .map(KafkaRecordPublisher::buildMessage);
  }

  private static Message<String> buildMessage(KafkaConsumerRecord<String, String> record) {
    KafkaContext kafkaContext = new KafkaContextImpl(record);

    KafkaTarget kafkaTarget = new KafkaTargetImpl(null, null, kafkaContext.headers());

    Message<String> msg = Message.<String> builder()
        .addMessageContext(kafkaContext)
        .addMessageContext(kafkaTarget)
        .payload(record.value())
        .build();

    msg.getMessageAck()
        .whenComplete((v, t) -> handleCompletion(kafkaContext, t));

    return msg;
  }

  private static void handleCompletion(KafkaContext kafkaContext, Throwable t) {
    if (t != null) {
      logger.warn("Kafka Message exceptionally completed topic={} partition={} offset={}", kafkaContext.topic(),
          kafkaContext.partition(), kafkaContext.offset());
      // Call the error handling mechanism...
      return;
    }
    logger.debug("Kafka Message completed topic={} partition={} offset={}", kafkaContext.topic(),
        kafkaContext.partition(), kafkaContext.offset());
    // Here, the commit logic could be triggered
  }
}
