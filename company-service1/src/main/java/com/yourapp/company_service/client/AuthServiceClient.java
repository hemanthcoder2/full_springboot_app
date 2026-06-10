package com.yourapp.company_service.client;

import com.yourapp.company_service.dto.AuthServiceResponse;
import com.yourapp.company_service.dto.RegisterAdminRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.HttpHeaders;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceClient {
    private final RestTemplate restTemplate;

    @Value("${services.auth-url}")
    private String authServiceUrl;

    public Long registerAdmin(RegisterAdminRequest request){
        try {
            String url = authServiceUrl + "/api/v1/auth/register";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<RegisterAdminRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<AuthServiceResponse> response = restTemplate.postForEntity(url, entity, AuthServiceResponse.class);
            if (response.getStatusCode().is2xxSuccessful()
                    && response.getBody() != null
                    && response.getBody().getData() != null) {
                return response.getBody().getData().getId();
            }
        } catch (RuntimeException e) {
            throw e; // rethrow your own exceptions
        } catch (Exception e) {
            log.error("Auth Service call failed: {}", e.getMessage());
            throw new RuntimeException("Auth Service is unavailable");
        }
        throw new RuntimeException("Failed to register admin user in Auth Service");

    }
}
