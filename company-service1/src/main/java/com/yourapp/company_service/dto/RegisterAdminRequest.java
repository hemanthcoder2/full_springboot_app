package com.yourapp.company_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterAdminRequest {
    private String fullName;
    private String email;
    private String password;
    private Long companyId;
    private String role;
}
