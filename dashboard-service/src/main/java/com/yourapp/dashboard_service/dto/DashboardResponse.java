package com.yourapp.dashboard_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponse {
    private Long id;
    private String title;
    private String description;
    private Long companyId;
    private String status;
    private Long createdBy;
    private String createdAt;

}
