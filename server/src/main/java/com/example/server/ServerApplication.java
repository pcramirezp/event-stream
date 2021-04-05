package com.example.server;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Collections;

@SpringBootApplication
@EnableScheduling
public class ServerApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ServerApplication.class)
                .properties(Collections.singletonMap("server.port", "8080"))
                .run(args);
    }
}
