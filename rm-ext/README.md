# Reactive Extension for Quarkus

This is a very first tentative implementation:

* `Message` and `MessageContext` injection work either using regular Quarkus DI or direct injection, including in native mode.

* Async<X> are not supported for now.

* Few testing so far.


It can be installed locally:

```java
mvn clean install
```

Then, the `quarkus-app` module can be started in native mode the regular way:

```java
 ./mvnw clean package -Pnative
./target/quarkus-app-0.0.1-SNAPSHOT-runner
2020-01-13 14:29:58,992 WARN  [com.ama.mid.ody.rea.mes.qua.rm.QuarkusFunctionInvoker] (main) null parameter injections for class com.amadeus.middleware.odyssey.quarkusapp.Processor.processor1 com.amadeus.middleware.odyssey.reactive.messaging.core.Async com.amadeus.middleware.odyssey.reactive.messaging.core.Async with type=class java.lang.String
2020-01-13 14:29:58,994 INFO  [com.ama.mid.ody.qua.Processor] (main) processor1 {messageContexts=[MyMessageContextImpl{text='Hello world!'}], payload=hello}
2020-01-13 14:29:58,994 INFO  [com.ama.mid.ody.qua.Processor] (main) processor1 di injected {messageContexts=[MyMessageContextImpl{text='Hello world!'}], payload=hello}
2020-01-13 14:29:58,994 INFO  [com.ama.mid.ody.qua.Processor] (main) processor1 mmc MyMessageContextImpl{text='Hello world!'}
2020-01-13 14:29:58,994 INFO  [com.ama.mid.ody.qua.Processor] (main) processor2 hello
2020-01-13 14:29:58,994 INFO  [com.ama.mid.ody.qua.Processor] (main) myMessageContext MyMessageContextImpl{text='Hello world!'}
2020-01-13 14:29:58,994 WARN  [com.ama.mid.ody.rea.mes.qua.rm.QuarkusFunctionInvoker] (main) null parameter injections for class com.amadeus.middleware.odyssey.quarkusapp.Processor.terminalProcessor com.amadeus.middleware.odyssey.reactive.messaging.core.Async com.amadeus.middleware.odyssey.reactive.messaging.core.Async with type=class java.lang.String
2020-01-13 14:29:58,995 INFO  [com.ama.mid.ody.qua.Processor] (main) outGoingProcessor {messageContexts=[MyMessageContextImpl{text='Hello world!'}], payload=hello}
2020-01-13 14:29:58,995 INFO  [com.ama.mid.ody.rea.mes.cor.rea.ReactiveStreamBuilderVisitor] (main) Stream end
2020-01-13 14:29:58,995 INFO  [io.quarkus] (main) quarkus-app 0.0.1-SNAPSHOT (running on Quarkus 1.1.1.Final) started in 0.033s. Listening on: http://0.0.0.0:8080
2020-01-13 14:29:58,996 INFO  [io.quarkus] (main) Profile prod activated. 
2020-01-13 14:29:58,996 INFO  [io.quarkus] (main) Installed features: [cdi, resteasy, rm-ext, smallrye-context-propagation, smallrye-reactive-streams-operators]
```
