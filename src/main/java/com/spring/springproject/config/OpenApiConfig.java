package com.spring.springproject.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Госуслуги API",
                version = "1.0",
                description = "API для системы государственных услуг"
        )
)
public class OpenApiConfig {
    // Убираем дублирующий bean customOpenAPI, так как вся конфигурация уже в аннотации
}
