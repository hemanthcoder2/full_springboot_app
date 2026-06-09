package com.yourapp.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@Order(-1)
@Slf4j
public class GlobalErrorHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        HttpStatus status;
        String message;

        if (ex instanceof NotFoundException) {
            status  = HttpStatus.SERVICE_UNAVAILABLE;
            message = "Service is temporarily unavailable. Please try again later.";
            log.error("Service not found / unreachable: {}", ex.getMessage());

        } else if (ex instanceof ResponseStatusException rse) {
            status  = HttpStatus.valueOf(rse.getStatusCode().value());
            message = rse.getReason() != null ? rse.getReason() : ex.getMessage();

        } else {
            status  = HttpStatus.INTERNAL_SERVER_ERROR;
            message = "An unexpected error occurred in the gateway";
            log.error("Unhandled gateway error: {}", ex.getMessage(), ex);
        }

        response.setStatusCode(status);

        String body = String.format(
            "{\"success\":false,\"message\":\"%s\",\"status\":%d}",
            message, status.value()
        );

        DataBuffer buffer = response.bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }
}
