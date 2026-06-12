package com.yourapp.dashboard_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateDashboardRequest {
    private String title;
    private String description;
}
