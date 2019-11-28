package com.amadeus.middleware.odyssey.reactive.messaging.kafka.connector.testsender;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class KafkaSender {

  public static void main(String args[]) {
    Map<String, String> config = new HashMap<>();
    config.put("bootstrap.servers", "127.0.0.1:9092");
    config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    config.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    KafkaProducer<String, String> producer = new KafkaProducer(config);
    producer.send(new ProducerRecord("mytopic", "key1", "123-value1"));
    producer.send(new ProducerRecord("mytopic", "key2", "124-value2"));
    producer.send(new ProducerRecord("mytopic", "key3", "125-value3"));
    producer.flush();
    producer.close();
  }

}
