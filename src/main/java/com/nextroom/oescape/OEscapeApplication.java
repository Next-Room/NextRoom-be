package com.nextroom.oescape;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class OEscapeApplication {

    public static void main(String[] args) {
        SpringApplication.run(com.nextroom.oescape.OEscapeApplication.class, args);
    }

}