package com.spring.springproject.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "User API",
                description = "User API for Admin", version = "1.0.0",
                termsOfService = "http://localhost:8080"
        )
)
public class OpenApiConfig {

}
