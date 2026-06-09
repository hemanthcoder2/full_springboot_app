package com.yourapp.gateway.filter;

import com.yourapp.gateway.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends AbstractGatewayFilterFactory<JwtAuthFilter.Config> {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter() {
        super(Config.class);
        this.jwtUtil = null;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();

            // ── 1. Extract Authorization header ───────────────────
            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("Missing or malformed Authorization header for path: {}", path);
                return onError(exchange, "Authorization header is missing or invalid", HttpStatus.UNAUTHORIZED);
            }

            String token = authHeader.substring(7);

            // ── 2. Validate JWT ────────────────────────────────────
            if (!jwtUtil.isTokenValid(token)) {
                log.warn("Invalid JWT token for path: {}", path);
                return onError(exchange, "JWT token is invalid or expired", HttpStatus.UNAUTHORIZED);
            }

            // ── 3. Extract claims and forward as headers ───────────
            try {
                Claims claims = jwtUtil.getClaims(token);
                String email     = claims.getSubject();
                String role      = claims.get("role", String.class);
                Long   companyId = claims.get("companyId", Long.class);

                // Forward user context to downstream services via request headers
                // Downstream services trust these headers (they come from gateway only)
                ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                        .header("X-User-Email",     email)
                        .header("X-User-Role",      role)
                        .header("X-Company-Id",     String.valueOf(companyId))
                        .build();

                log.debug("Forwarding request — user: {}, role: {}, company: {}, path: {}",
                        email, role, companyId, path);

                return chain.filter(exchange.mutate().request(mutatedRequest).build());

            } catch (Exception e) {
                log.error("Failed to extract JWT claims: {}", e.getMessage());
                return onError(exchange, "Failed to process token", HttpStatus.UNAUTHORIZED);
            }
        };
    }

    /**
     * Write a JSON error response and stop the filter chain.
     */
    private Mono<Void> onError(ServerWebExchange exchange,
                                String message,
                                HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String body = String.format(
            "{\"success\":false,\"message\":\"%s\",\"status\":%d}",
            message, status.value()
        );

        DataBuffer buffer = response.bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }

    public static class Config {
        // No config needed for now — extend later for role-based route restrictions
    }
}
