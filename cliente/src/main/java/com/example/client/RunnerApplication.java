package com.example.client;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.logging.Logger;

@Configuration
@EnableAsync
public class RunnerApplication {
    private static final Logger logger = Logger.getLogger(RunnerApplication.class.getName());

    @Bean
    WebClient client() {
        return WebClient.create("http://localhost:8080");
    }

    @Bean
    CommandLineRunner demo(WebClient client) {
        return args -> {
            run(client);
            //         run(client);
            //        run(client);
            //       run(client);
            //      run(client);
        };
    }

    private void run(WebClient client) {
        Mono.defer(() -> client.get().uri("/events")
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange())
                .retryWhen(Retry.backoff(10, Duration.ofSeconds(2)))
                .flatMapMany(cr -> cr.bodyToFlux(Event.class))
                .doOnError(throwable -> logger.info("doOnError: " + throwable.getMessage()))
                .doOnNext(event -> {
                    var name = Thread.currentThread().getName();
                    logger.info(name + " - " + event.toString());
                })
                .subscribeOn(Schedulers.parallel()).subscribe();
    }

}
