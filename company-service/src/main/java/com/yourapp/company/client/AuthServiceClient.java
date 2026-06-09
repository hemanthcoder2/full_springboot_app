package com.yourapp.company.client;

import com.yourapp.company.dto.CompanyDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthServiceClient {

    private final RestTemplate restTemplate;

    @Value("${services.auth-url}")
    private String authServiceUrl;

    /**
     * Calls POST /api/v1/auth/register on the Auth Service.
     * Creates the admin user for a newly registered company.
     */
    public CompanyDto.AuthServiceResponse registerUser(CompanyDto.RegisterUserRequest request) {
        String url = authServiceUrl + "/api/v1/auth/register";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CompanyDto.RegisterUserRequest> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<CompanyDto.AuthServiceResponse> response =
                restTemplate.postForEntity(url, entity, CompanyDto.AuthServiceResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Auth Service registered user: {}", request.getEmail());
                return response.getBody();
            }

            throw new RuntimeException("Auth Service returned unexpected status: " + response.getStatusCode());

        } catch (HttpClientErrorException e) {
            log.error("Auth Service error: {} — {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Failed to create admin user: " + e.getMessage());
        } catch (Exception e) {
            log.error("Auth Service unreachable: {}", e.getMessage());
            throw new RuntimeException("Auth Service is unavailable. Please try again.");
        }
    }
}
