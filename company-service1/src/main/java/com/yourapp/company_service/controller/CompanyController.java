package com.yourapp.company_service.controller;

import com.yourapp.company_service.dto.ApiResponse;
import com.yourapp.company_service.dto.CompanyResponse;
import com.yourapp.company_service.dto.CreateCompanyRequest;
import com.yourapp.company_service.dto.UpdateCompanyRequest;
import com.yourapp.company_service.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
public class CompanyController {
    private final CompanyService companyService;

    @PostMapping
    public ResponseEntity<ApiResponse> createCompany(@Valid @RequestBody CreateCompanyRequest request){
       CompanyResponse companyResponse= companyService.createCompany(request);
       return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Company Created", companyResponse));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getCompany(@PathVariable Long id){
        CompanyResponse companyResponse= companyService.getCompany(id);
        return ResponseEntity.ok(ApiResponse.success("Company Found", companyResponse));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateCompany(@PathVariable Long id, @Valid @RequestBody UpdateCompanyRequest request){
        CompanyResponse companyResponse= companyService.updateCompany(id, request);
        return ResponseEntity.ok(ApiResponse.success("Company Updated", companyResponse));
    }

}
