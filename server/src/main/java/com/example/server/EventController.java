package com.example.server;

import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Logger;

@RestController
@CrossOrigin(origins = "*")
public class EventController {
    Queue<Event> queue = new ConcurrentLinkedDeque<>();
    Logger logger = Logger.getLogger(EventController.class.getName());
    @GetMapping(produces = MediaType.APPLICATION_STREAM_JSON_VALUE, value = "/events")
    public Flux<Event> events() {
        logger.info("New Connection!!!");
        Flux<Long> durationFlux = Flux.interval(Duration.ofMillis(500));

        return durationFlux
                .subscribeOn(Schedulers.elastic())
                .filter(ignore -> !queue.isEmpty())
                .flatMap(ignore -> Mono.just(Objects.requireNonNull(queue.poll())))
                .switchIfEmpty(Mono.defer(Mono::empty));
    }

    @Async
    @EventListener
    public void handleMessage(Event event) {
        queue.add(event);
    }
}
