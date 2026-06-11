package com.yourapp.employee_service.controller;

import com.yourapp.employee_service.dto.ApiResponse;
import com.yourapp.employee_service.dto.CreateDepartmentRequest;
import com.yourapp.employee_service.dto.DepartmentResponse;
import com.yourapp.employee_service.service.DepartmentService;
import com.yourapp.employee_service.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/departments")
public class DepartmentController {
    private final DepartmentService departmentService;

    @PostMapping
    public ResponseEntity<ApiResponse> createDepartment(@Valid @RequestBody CreateDepartmentRequest request, @RequestHeader("X-Company-Id") Long companyId){
        DepartmentResponse response = departmentService.createDepartment(request,companyId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Department Created Successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getDepartment(@RequestHeader("X-Company-Id") Long companyId){
        List<DepartmentResponse> response = departmentService.getDepartmentsByCompany(companyId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Department Found", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteDepartment(@PathVariable Long id, @RequestHeader("X-Company-Id") Long companyId){
        departmentService.deleteDepartment(id, companyId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Deleted Successfully", true));
    }

}
