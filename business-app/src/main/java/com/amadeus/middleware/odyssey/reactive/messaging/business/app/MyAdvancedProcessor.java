package com.amadeus.middleware.odyssey.reactive.messaging.business.app;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Async;
import com.amadeus.middleware.odyssey.reactive.messaging.core.Message;
import com.amadeus.middleware.odyssey.reactive.messaging.core.NodeName;
import com.amadeus.middleware.odyssey.reactive.messaging.corporate.framework.EventMetadata;
import com.amadeus.middleware.odyssey.reactive.messaging.kafka.connector.provider.KafkaIncoming;

import io.reactivex.Flowable;

@ApplicationScoped
public class MyAdvancedProcessor {
  private static final Logger logger = LoggerFactory.getLogger(MyAdvancedProcessor.class);

  @Inject
  private KafkaIncoming gkc;

  @Inject
  private Async<KafkaIncoming> agkc;

  @Inject
  private EventMetadata gec;

  @Inject
  private Async<EventMetadata> agec;

  @Inject
  private Message<String> gmsg;

  @Inject
  private Async<Message<String>> agmsg;

  @Incoming("internal_channel")
  @Outgoing("kafka_channel")
  @NodeName("stage4")
  public void stage4(KafkaIncoming kc, Async<KafkaIncoming> akc, EventMetadata ec, Async<EventMetadata> aec,
                     Message<String> msg, Async<Message<String>> amsg, Object payload) {

    logger.info("stage4 start");

    // Log objects coming from direct injection and CDI
    logger.info("KafkaIncoming direct={},cdi={}", kc, gkc);
    logger.info("EventMetadata direct={},cdi={}", ec, gec);
    logger.info("Message.payload direct={},cdi={}", msg.getPayload(), gmsg.getPayload());

    // Log the payload
    logger.info("Payload direct={}", payload);

    // Log the objects coming from Async<> either by direct injection or CDI
    EventMetadata asyncDirectEventMetadata = aec.get();
    EventMetadata asyncCdiEventMetadata = agec.get();
    logger.info("EventMetadata async direct={},cdi={}", asyncDirectEventMetadata, asyncCdiEventMetadata);

    // Updating the EventMetadata directly using the POJO
    asyncCdiEventMetadata.setUniqueMessageId("pojo-" + asyncCdiEventMetadata.getUniqueMessageId());
    logger.info("EventMetadata after POJO update direct={},cdi={}", ec, gec);

    // Yet another way, without injection, to get the KafkaIncoming
    KafkaIncoming kc2 = msg.<KafkaIncoming>getMetadata(KafkaIncoming.META_KEY);
    logger.debug("KafkaIncoming from message={}", kc2);

    // Let's start asynchronous processing with the message
    sendToAsynchronousProcessing(amsg);

    logger.info("stage4 stop");
  }

  private void sendToAsynchronousProcessing(Async<Message<String>> amsg) {

    Message<String> pojoMessage = agmsg.get();

    // Catch the acknowledgement to add the delay async operation as a condition in the middle
    // of the acknowledgement chain
    CompletableFuture<Void> newAcknowledger = new CompletableFuture<>();
    CompletableFuture<Void> incomingAcknowledger = pojoMessage.getAndSetStagedAck(newAcknowledger);

    Flowable.fromArray("hello")
        .delay(1, TimeUnit.SECONDS)
        .subscribe(txt -> {
          logger.info("Async operation completed: {} {}", txt, pojoMessage);
          newAcknowledger.whenComplete(logAndPropagate(incomingAcknowledger));
        });
  }

  private static BiConsumer<Void, Throwable> logAndPropagate(CompletionStage<Void> incomingAcknowledger) {
    return (c, e) -> {
      if (e == null) {
        logger.info("completing");
        incomingAcknowledger.toCompletableFuture()
            .complete(null);
      } else {
        logger.info("completing exceptionally");
        incomingAcknowledger.toCompletableFuture()
            .completeExceptionally(e);
      }
    };
  }
}
