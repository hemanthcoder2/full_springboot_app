package com.yourapp.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Configuration
public class GatewayConfig {

    /**
     * Rate limit key by IP address.
     * Used for public routes (login, register) where no user is known yet.
     */
    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            String ip = exchange.getRequest().getRemoteAddress() != null
                    ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                    : "unknown";
            return Mono.just(ip);
        };
    }

    /**
     * Rate limit key by authenticated user email (from X-User-Email header).
     * Falls back to IP if the header is not present.
     * Used for all protected routes.
     */
    @Bean
    @Primary
    public KeyResolver userKeyResolver() {
        return exchange -> {
            String userEmail = exchange.getRequest()
                    .getHeaders()
                    .getFirst("X-User-Email");

            if (userEmail != null && !userEmail.isBlank()) {
                return Mono.just(userEmail);
            }

            // fallback to IP
            String ip = exchange.getRequest().getRemoteAddress() != null
                    ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                    : "anonymous";
            return Mono.just(ip);
        };
    }
}
