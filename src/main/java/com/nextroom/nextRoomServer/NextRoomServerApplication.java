package com.nextroom.nextRoomServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class NextRoomServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(NextRoomServerApplication.class, args);
    }

}