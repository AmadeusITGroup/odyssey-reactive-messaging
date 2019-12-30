package com.amadeus.middleware.odyssey.reactive.messaging.business.app;

import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;
import com.amadeus.middleware.odyssey.reactive.messaging.kafka.connector.provider.KafkaTarget;

import io.reactivex.Flowable;

@ApplicationScoped
public class MyRxJavaProcessor {
  private static final Logger logger = LoggerFactory.getLogger(MyRxJavaProcessor.class);

  @SuppressWarnings("unchecked")
  @Incoming("rxin")
  @Outgoing("output_channel")
  public Publisher<Message<String>> stage6(Publisher<Message<String>> publisher) {
    return Flowable.fromPublisher(publisher)
        .flatMap(message -> {

          Message<String> child = Message.<String> builder()
              .fromParent(message)
              .payload(message.getPayload())
              .build();

          KafkaTarget target = child.getMessageContext(KafkaTarget.KEY);
          target.topic(target.topic() + "-child");

          return Flowable.fromArray(message, child);
        })
        .delay(1, TimeUnit.SECONDS);
  }
}
