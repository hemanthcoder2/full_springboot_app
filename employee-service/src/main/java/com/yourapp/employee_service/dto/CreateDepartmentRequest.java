package com.yourapp.employee_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateDepartmentRequest {
    @NotBlank(message = "Department name is required")
    private String name;
    private String description;
}
