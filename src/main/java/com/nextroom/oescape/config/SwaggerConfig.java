package com.nextroom.oescape.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig implements WebMvcConfigurer {

    @Bean
    public OpenAPI api() {
        return new OpenAPI()
            .info(this.apiInfo());
    }

    private Info apiInfo() {
        return new Info()
            .title("O Escape Api Server")
            .description("Nexters 23th 오늘의 방탈출 -NextRoom")
            .version("1.0")
            .contact(new Contact()
                .name("O Escape Server Github Repository")
                .url("https://github.com/Nexters/o-escape-be"));
    }
}