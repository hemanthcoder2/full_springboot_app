package com.yourapp.employee_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateEmployeeRequest {
    private String firstName;
    private String lastName;
    private String phone;
    private String designation;
    private Long departmentId;
    private Long teamId;
}
