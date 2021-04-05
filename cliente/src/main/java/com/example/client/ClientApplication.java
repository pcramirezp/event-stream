package com.example.client;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.util.Collections;

@SpringBootApplication
public class                                                                                                                                                                                                                                          ClientApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ClientApplication.class)
                .properties(Collections.singletonMap("server.port", "8081"))
                .run(args);
    }
}
