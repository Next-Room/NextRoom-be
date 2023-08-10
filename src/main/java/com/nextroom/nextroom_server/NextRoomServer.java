package com.nextroom.nextroom_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class NextRoomServer {

    public static void main(String[] args) {
        SpringApplication.run(NextRoomServer.class, args);
    }

}