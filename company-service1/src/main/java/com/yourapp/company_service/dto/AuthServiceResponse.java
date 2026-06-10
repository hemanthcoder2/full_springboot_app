package com.yourapp.company_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthServiceResponse {
    private boolean success;
    private String message;
    private UserData data;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class UserData {
        private Long id;
        private String email;
        private String fullName;
        private Long companyId;
        private String role;
    }
}