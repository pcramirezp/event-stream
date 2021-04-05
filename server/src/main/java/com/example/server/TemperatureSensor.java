package com.example.server;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.Random;

@Component
public class TemperatureSensor {
    private final ApplicationEventPublisher publisher;
    private final Random rnd = new Random();

    public TemperatureSensor(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }


    @Scheduled(fixedDelay=500)
    public void probe() {
        double temperature = 16 + rnd.nextGaussian() * 10;
        publisher.publishEvent(new Event(System.currentTimeMillis(), new Date(), temperature));
    }
}
