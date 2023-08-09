package com.nextroom.oescape.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig implements WebMvcConfigurer {

    @Value("${spring.config.activate.on-profile}")
    private String springProfilesActive;

    @Bean
    public OpenAPI api() {
        return new OpenAPI()
            .info(this.apiInfo());
    }

    private Info apiInfo() {
        return new Info()
            .title("[" + springProfilesActive + "] " + "NextRoom Api Server")
            .description("Nexters 23th 넥스트룸(NextRoom) | 방탈출 힌트폰")
            .version("1.0")
            .contact(new Contact()
                .name("NextRoom Server Github Repository")
                .url("https://github.com/Nexters/NextRoom-be"));
    }
}