package com.yourapp.company_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateCompanyRequest {

    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotBlank(message = "Email is Required")
    @Email(message = "Invalid Email")
    private String companyEmail;
    private String phone;
    private String address;
    private String industry;
    @NotBlank(message = "admin name is required")
    private String adminFullName;
    @NotBlank(message = "admin email is required")
    @Email(message = "Invalid adminEmail")
    private String adminEmail;
    @NotBlank(message = "adminPassword is required")
    private String adminPassword;
    private Long planId;
}
