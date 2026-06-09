# Auth Service

Spring Boot microservice handling authentication — JWT login, logout, and token refresh.

## Stack
- Java 17 + Spring Boot 3.2
- Spring Security + JWT (jjwt 0.11.5)
- MySQL 8.0
- Docker + Jenkins

## Project structure

```
auth-service/
├── src/main/java/com/yourapp/auth/
│   ├── AuthServiceApplication.java
│   ├── config/
│   │   ├── SecurityConfig.java        # Spring Security + JWT filter chain
│   │   ├── SwaggerConfig.java         # OpenAPI/Swagger setup
│   │   └── GlobalExceptionHandler.java
│   ├── controller/
│   │   └── AuthController.java        # REST endpoints
│   ├── dto/
│   │   └── AuthDto.java               # Request + Response DTOs
│   ├── entity/
│   │   ├── User.java                  # users table
│   │   └── RefreshToken.java          # refresh_tokens table
│   ├── repository/
│   │   ├── UserRepository.java
│   │   └── RefreshTokenRepository.java
│   ├── security/
│   │   ├── JwtUtil.java               # Token generate / validate
│   │   ├── JwtAuthFilter.java         # OncePerRequestFilter
│   │   └── CustomUserDetailsService.java
│   └── service/
│       └── AuthService.java           # Business logic
├── src/main/resources/
│   └── application.yml
├── Dockerfile
├── docker-compose.yml
└── Jenkinsfile
```

## Running locally

### Option 1 — Docker Compose (recommended)
```bash
docker-compose up -d
```
Service starts on http://localhost:8081

### Option 2 — Maven directly
```bash
# Start MySQL first (or use your local MySQL)
mvn spring-boot:run
```

## Environment variables

| Variable | Default | Description |
|---|---|---|
| `DB_USERNAME` | `root` | MySQL username |
| `DB_PASSWORD` | `root` | MySQL password |
| `JWT_SECRET` | (set in yml) | Base64-encoded HMAC secret |

## API endpoints

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/api/v1/auth/login` | Public | Login, returns access + refresh token |
| POST | `/api/v1/auth/refresh` | Public | Exchange refresh token for new access token |
| POST | `/api/v1/auth/logout` | Bearer | Revoke all refresh tokens |
| POST | `/api/v1/auth/register` | Public | Register user (called by Company service) |
| GET  | `/api/v1/auth/me` | Bearer | Current user info |

## Swagger UI
http://localhost:8081/swagger-ui.html

## JWT payload

```json
{
  "sub": "user@example.com",
  "companyId": 1,
  "role": "ADMIN",
  "iat": 1700000000,
  "exp": 1700000900
}
```

## Token lifetimes
- Access token: **15 minutes**
- Refresh token: **7 days** (rotated on every use)
