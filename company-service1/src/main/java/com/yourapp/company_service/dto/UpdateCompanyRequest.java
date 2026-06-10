package com.yourapp.company_service.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompanyRequest {
    private String name;
    private String phone;
    private String address;
    private String industry;
    private String logoUrl;
}
