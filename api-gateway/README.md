# API Gateway

Spring Cloud Gateway — single entry point for all microservices.

## What it does
- Routes every request to the correct downstream service
- Validates JWT on every protected route before forwarding
- Injects user context (email, role, companyId) as request headers for downstream services
- Rate limits by IP (public routes) and by user (protected routes) using Redis
- Handles CORS globally so downstream services don't need to
- Logs every request/response with timing

## Stack
- Java 17 + Spring Boot 3.2 + Spring Cloud Gateway
- Reactive (Netty, not Tomcat)
- Redis for rate limiting
- jjwt for JWT parsing
- Docker + Jenkins

## Port map

| Service | Port |
|---|---|
| API Gateway | **8080** ← all traffic enters here |
| Auth Service | 8081 |
| Company Service | 8082 |
| Employee Service | 8083 |
| Team Service | 8084 |
| Dashboard Service | 8085 |
| Role Service | 8086 |
| Pricing Service | 8087 |

## How JWT validation works

```
Request → Gateway
  ↓
JwtAuthFilter
  1. Read Authorization: Bearer <token>
  2. Validate signature + expiry with HMAC-SHA256
  3. Extract: email, role, companyId from claims
  4. Mutate request — add headers:
       X-User-Email:  admin@acme.com
       X-User-Role:   ADMIN
       X-Company-Id:  1
  5. Forward to downstream service
```

Downstream services read `X-User-Email`, `X-User-Role`, `X-Company-Id` headers.
They trust these headers because they only accept traffic from the gateway (internal network).

## Rate limits

| Route type | Limit |
|---|---|
| Public (login, register) | 10 req/sec per IP, burst 20 |
| Protected (employees, teams) | 30 req/sec per user, burst 60 |
| Company, pricing | 20 req/sec per user, burst 40 |

## Running locally

```bash
# Start Redis + Gateway
docker-compose up -d

# Or start the full platform (gateway + auth + all services)
docker-compose -f docker-compose-full.yml up -d
```

## Routes

| Path pattern | Service | Auth required |
|---|---|---|
| /api/v1/auth/login | auth-service | No |
| /api/v1/auth/refresh | auth-service | No |
| /api/v1/auth/register | auth-service | No |
| /api/v1/auth/** | auth-service | Yes |
| /api/v1/companies/** | company-service | Yes |
| /api/v1/employees/** | employee-service | Yes |
| /api/v1/departments/** | employee-service | Yes |
| /api/v1/teams/** | team-service | Yes |
| /api/v1/dashboards/** | dashboard-service | Yes |
| /api/v1/roles/** | role-service | Yes |
| /api/v1/plans/** | pricing-service | Yes |
| /api/v1/subscriptions/** | pricing-service | Yes |

## Health check
```
GET http://localhost:8080/actuator/health
GET http://localhost:8080/actuator/gateway/routes
```
