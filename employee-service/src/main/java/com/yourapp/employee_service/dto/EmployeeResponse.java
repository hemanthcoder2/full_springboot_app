package com.yourapp.employee_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String designation;
    private String departmentName;
    private Long departmentId;
    private Long teamId;
    private Long companyId;
    private String status;
    private String createdAt;
}
