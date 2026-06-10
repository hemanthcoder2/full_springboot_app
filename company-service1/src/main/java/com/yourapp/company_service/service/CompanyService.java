package com.yourapp.company_service.service;

import com.yourapp.company_service.client.AuthServiceClient;
import com.yourapp.company_service.dto.CompanyResponse;
import com.yourapp.company_service.dto.CreateCompanyRequest;
import com.yourapp.company_service.dto.RegisterAdminRequest;
import com.yourapp.company_service.dto.UpdateCompanyRequest;
import com.yourapp.company_service.entity.Company;
import com.yourapp.company_service.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyService {
    private final CompanyRepository companyRepository;

    private final AuthServiceClient authServiceClient;


    public CompanyResponse createCompany(CreateCompanyRequest request) {
        if(companyRepository.existsByEmail(request.getCompanyEmail())) {
            throw new RuntimeException("company already exists");
        }
        log.info("Creating company with email: {}", request.getCompanyEmail());


        Company company = Company.builder()
                .name(request.getCompanyName())
                .email(request.getCompanyEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .industry(request.getIndustry())
                .build();
        company = companyRepository.save(company);
        RegisterAdminRequest adminRequest = RegisterAdminRequest.builder()
                .fullName(request.getAdminFullName())
                .email(request.getAdminEmail())
                .password(request.getAdminPassword())
                .companyId(company.getId())
                .role("ADMIN")
                .build();

        Long adminUserId = authServiceClient.registerAdmin(adminRequest);
        log.info("Admin registered with id: {}", adminUserId);
        company.setAdminUserId(adminUserId);
        company = companyRepository.save(company);

        log.info("company created with email: {}", request.getCompanyEmail());
        return toResponse(company);
    }

    public CompanyResponse updateCompany(Long id, UpdateCompanyRequest req) {
        Company company = companyRepository.findById(id).orElseThrow(() -> new RuntimeException("company not found"));
        if (req.getName()     != null) company.setName(req.getName());
        if (req.getPhone()    != null) company.setPhone(req.getPhone());
        if (req.getAddress()  != null) company.setAddress(req.getAddress());
        if (req.getIndustry() != null) company.setIndustry(req.getIndustry());
        if (req.getLogoUrl()  != null) company.setLogoUrl(req.getLogoUrl());

       company= companyRepository.save(company);
        log.info("Updated company with Id: {}", id);
       return toResponse(company);

    }

    public CompanyResponse getCompany(Long companyId) {
        Company company = companyRepository.findById(companyId).orElseThrow(()  -> new RuntimeException("company not found"));
        log.info("Getting company with id: {}", companyId);
        return toResponse(company);
    }


    // Helper method
    private CompanyResponse toResponse(Company company) {
        CompanyResponse companyResponse = CompanyResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .email(company.getEmail())
                .phone(company.getPhone())
                .address(company.getAddress())
                .industry(company.getIndustry())
                .logoUrl(company.getLogoUrl())
                .adminUserId(company.getAdminUserId())
                .status(company.getStatus().name())
                .createdAt(company.getCreatedAt() != null ? company.getCreatedAt().toString() : null)
                .build();

        return companyResponse;

    }

}
