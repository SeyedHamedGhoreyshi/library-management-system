package com.library.infrastructure.web.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String BEARER_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI libraryManagementOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Library Management System API")
                        .description("REST API for managing a library catalog: registering, updating, " +
                                "and soft-deleting books, borrowing/returning books, and catalog search.")
                        .version("v1")
                        .contact(new Contact().name("Library Management System")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(BEARER_SCHEME_NAME, new SecurityScheme()
                                .name(BEARER_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Paste the JWT returned by POST /api/v1/auth/login or /api/v1/auth/signup")));
    }
}