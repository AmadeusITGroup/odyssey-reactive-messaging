package com.amadeus.middleware.odyssey.reactive.messaging.kafka.connector.provider;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;
import com.amadeus.middleware.odyssey.reactive.messaging.core.NodeName;

import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.kafka.client.consumer.KafkaConsumer;
import io.vertx.reactivex.kafka.client.consumer.KafkaConsumerRecord;

@ApplicationScoped
public class KafkaRecordPublisher {
  private static final Logger logger = LoggerFactory.getLogger(KafkaRecordPublisher.class);

  @Outgoing("input_channel")
  @NodeName("KafkaPublisher")
  public Publisher<Message<String>> publish() {
    Vertx vertx = Vertx.vertx();

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
    KafkaIncoming kafkaIncoming = new KafkaIncomingImpl(record);

    KafkaTarget kafkaTarget = new KafkaTargetImpl(null, null, kafkaIncoming.headers());

    Message<String> msg = Message.<String> builder()
        .addMetadata(kafkaIncoming)
        .addMetadata(kafkaTarget)
        .payload(record.value())
        .build();

    msg.getMessageAck()
        .whenComplete((v, t) -> handleCompletion(kafkaIncoming, t));

    return msg;
  }

  private static void handleCompletion(KafkaIncoming kafkaIncoming, Throwable t) {
    if (t != null) {
      logger.warn("Kafka Message exceptionally completed topic={} partition={} offset={}", kafkaIncoming.topic(),
          kafkaIncoming.partition(), kafkaIncoming.offset());
      // Call the error handling mechanism...
      return;
    }
    logger.debug("Kafka Message completed topic={} partition={} offset={}", kafkaIncoming.topic(),
        kafkaIncoming.partition(), kafkaIncoming.offset());
    // Here, the commit logic could be triggered
  }
}
