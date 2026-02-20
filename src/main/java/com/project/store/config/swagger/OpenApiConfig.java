package com.project.store.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("Zest India Store API")
                        .description("API documentation for Product, Item and User management")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Rahul Basutkar")
                                .email("rahulbasutkar33@gmail.com")
                                .url("https://rahulbasutkar04.github.io/rahulbasutkar.github.io/")
                        )
                );
    }
}