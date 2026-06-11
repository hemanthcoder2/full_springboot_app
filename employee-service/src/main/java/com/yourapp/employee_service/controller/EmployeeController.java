package com.yourapp.employee_service.controller;

import com.yourapp.employee_service.dto.*;
import com.yourapp.employee_service.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    @PostMapping
    public ResponseEntity<ApiResponse> createEmployee(@Valid @RequestBody CreateEmployeeRequest request, @RequestHeader("X-Company-Id") Long companyId
    ) {
        EmployeeResponse response = employeeService.createEmployee(request,companyId);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Employee Created", response));

    }


    @GetMapping
    public ResponseEntity<ApiResponse> getEmployees(@RequestHeader("X-Company-Id") Long companyId, @RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "firstName") String sort, @RequestParam(required = false) String search) {
        PagedResponse<EmployeeResponse> response = employeeService.getEmployees(companyId,page,size,sort,search);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Employees Found", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getEmployee(@PathVariable Long id,@RequestHeader("X-Company-Id") Long companyId) {
        EmployeeResponse response = employeeService.getEmployee(id,companyId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Employee Found", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateEmployee(@PathVariable Long id, @Valid @RequestBody UpdateEmployeeRequest request, @RequestHeader("X-Company-Id") Long companyId) {
        EmployeeResponse response = employeeService.updateEmployee(id,request,companyId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Employee Updated", response));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse> toggleStatus(@PathVariable Long id, @RequestHeader("X-Company-Id") Long companyId) {
        EmployeeResponse response = employeeService.toggleStatus(id,companyId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Employee Updated", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteEmployee(@PathVariable Long id,@RequestHeader("X-Company-Id") Long companyId) {
        employeeService.deleteEmployee(id,companyId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Employee Deleted Successfully", true));
    }
}
