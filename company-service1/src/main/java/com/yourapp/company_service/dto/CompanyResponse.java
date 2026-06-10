package com.yourapp.company_service.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String industry;
    private String logoUrl;
    private String status;
    private Long adminUserId;
    private String createdAt;
}
