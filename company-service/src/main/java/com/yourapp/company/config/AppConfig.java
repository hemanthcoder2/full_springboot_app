package com.yourapp.company.config;

import com.yourapp.company.dto.CompanyDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

// ── Global exception handler ──────────────────────────────────

@RestControllerAdvice
@Slf4j
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CompanyDto.ApiResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            errors.put(field, error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(
            CompanyDto.ApiResponse.builder().success(false).message("Validation failed").data(errors).build()
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<CompanyDto.ApiResponse> handleRuntime(RuntimeException ex) {
        log.error("Error: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(CompanyDto.ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CompanyDto.ApiResponse> handleGeneral(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CompanyDto.ApiResponse.error("An unexpected error occurred"));
    }
}
