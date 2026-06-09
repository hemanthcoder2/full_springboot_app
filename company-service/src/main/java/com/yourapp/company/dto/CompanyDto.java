package com.yourapp.company.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

public class CompanyDto {

    // ── Request DTOs ──────────────────────────────────────────────

    /**
     * Sent by the frontend when registering a new company.
     * Creates the company + the first admin user + a pricing plan in one shot.
     */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class CreateCompanyRequest {

        // Company details
        @NotBlank(message = "Company name is required")
        private String companyName;

        @NotBlank(message = "Company email is required")
        @Email(message = "Invalid company email")
        private String companyEmail;

        private String phone;
        private String address;
        private String industry;

        // Admin user details (first user of this company)
        @NotBlank(message = "Admin full name is required")
        private String adminFullName;

        @NotBlank(message = "Admin email is required")
        @Email(message = "Invalid admin email")
        private String adminEmail;

        @NotBlank(message = "Admin password is required")
        private String adminPassword;

        // Plan selection
        @NotNull(message = "Plan ID is required")
        private Long planId;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class UpdateCompanyRequest {
        private String name;
        private String phone;
        private String address;
        private String industry;
        private String logoUrl;
    }

    // ── Response DTOs ─────────────────────────────────────────────

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CompanyResponse {
        private Long   id;
        private String name;
        private String email;
        private String phone;
        private String address;
        private String industry;
        private String logoUrl;
        private String status;
        private Long   adminUserId;
        private String createdAt;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CreateCompanyResponse {
        private CompanyResponse company;
        private AdminUserInfo   adminUser;
        private String          message;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AdminUserInfo {
        private Long   id;
        private String email;
        private String fullName;
        private String role;
    }

    // ── Internal DTOs (sent to Auth Service) ─────────────────────

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class RegisterUserRequest {
        private String fullName;
        private String email;
        private String password;
        private Long   companyId;
        private String role;   // "ADMIN"
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AuthServiceResponse {
        private boolean success;
        private String  message;
        private UserData data;

        @Getter @Setter @NoArgsConstructor @AllArgsConstructor
        public static class UserData {
            private Long   id;
            private String email;
            private String fullName;
            private Long   companyId;
            private String role;
        }
    }

    // ── Internal DTOs (sent to Pricing Service) ───────────────────

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CreateSubscriptionRequest {
        private Long companyId;
        private Long planId;
    }

    // ── Standard API wrapper ──────────────────────────────────────

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ApiResponse {
        private boolean success;
        private String  message;
        private Object  data;

        public static ApiResponse success(String message, Object data) {
            return ApiResponse.builder().success(true).message(message).data(data).build();
        }
        public static ApiResponse error(String message) {
            return ApiResponse.builder().success(false).message(message).build();
        }
    }
}
