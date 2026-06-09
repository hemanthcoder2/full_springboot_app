package com.yourapp.company.controller;

import com.yourapp.company.dto.CompanyDto;
import com.yourapp.company.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
@Tag(name = "Company", description = "Company registration and management")
public class CompanyController {

    private final CompanyService companyService;

    /**
     * PUBLIC — called when a new business signs up.
     * Creates company + admin user + starts free trial in one request.
     */
    @PostMapping
    @Operation(summary = "Register a new company (public)")
    public ResponseEntity<CompanyDto.ApiResponse> createCompany(
            @Valid @RequestBody CompanyDto.CreateCompanyRequest request) {

        CompanyDto.CreateCompanyResponse result = companyService.createCompany(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CompanyDto.ApiResponse.success("Company registered successfully", result));
    }

    /**
     * Get company details.
     * Gateway injects X-Company-Id from the JWT — we read it here.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get company by ID")
    public ResponseEntity<CompanyDto.ApiResponse> getCompany(
            @PathVariable Long id,
            @RequestHeader("X-Company-Id") Long requestingCompanyId,
            @RequestHeader("X-User-Role")  String role) {

        // ADMIN can see any company; others can only see their own
        if (!role.equals("ADMIN") && !id.equals(requestingCompanyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(CompanyDto.ApiResponse.error("Access denied"));
        }

        CompanyDto.CompanyResponse response = companyService.getCompany(id);
        return ResponseEntity.ok(CompanyDto.ApiResponse.success("Company fetched", response));
    }

    /**
     * Update company details.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update company details")
    public ResponseEntity<CompanyDto.ApiResponse> updateCompany(
            @PathVariable Long id,
            @Valid @RequestBody CompanyDto.UpdateCompanyRequest request,
            @RequestHeader("X-Company-Id") Long requestingCompanyId,
            @RequestHeader("X-User-Role")  String role) {

        if (!role.equals("ADMIN") && !id.equals(requestingCompanyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(CompanyDto.ApiResponse.error("Access denied"));
        }

        CompanyDto.CompanyResponse response =
                companyService.updateCompany(id, request, requestingCompanyId);
        return ResponseEntity.ok(CompanyDto.ApiResponse.success("Company updated", response));
    }

    /**
     * Suspend a company (super-admin use only — no role check here for now).
     */
    @PatchMapping("/{id}/suspend")
    @Operation(summary = "Suspend a company")
    public ResponseEntity<CompanyDto.ApiResponse> suspendCompany(
            @PathVariable Long id,
            @RequestHeader("X-User-Role") String role) {

        if (!role.equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(CompanyDto.ApiResponse.error("Only ADMIN can suspend a company"));
        }

        companyService.suspendCompany(id);
        return ResponseEntity.ok(CompanyDto.ApiResponse.success("Company suspended", null));
    }
}
