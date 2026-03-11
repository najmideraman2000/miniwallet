package com.assessment.miniwallet.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI miniWalletOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Mini Wallet API")
                        .description("RESTful API documentation for Mini Wallet. " +
                                "Includes functionalities for creating users, checking balances, top-ups, and peer-to-peer transfers.")
                        .version("v0.0.1")
                        .contact(new Contact()
                                .name("Najmi Deraman")
                                .email("najmideraman2000@gmail.com")));
    }
}