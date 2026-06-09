package com.yourapp.company.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Company Service API",
        version = "1.0",
        description = "Company registration, tenant management"
    )
)
public class SwaggerConfig {}
