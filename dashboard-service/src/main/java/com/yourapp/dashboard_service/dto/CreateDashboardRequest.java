package com.yourapp.dashboard_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateDashboardRequest {

    @NotBlank(message="Title should not be empty")
    private String title;
    private String description;
}
