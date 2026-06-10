package com.yourapp.company_service.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Company Service API",
                version = "1.0",
                description = "Company registration and management"
        )
)
public class SwaggerConfig {
}