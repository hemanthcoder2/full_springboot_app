package com.yourapp.company.service;

import com.yourapp.company.client.AuthServiceClient;
import com.yourapp.company.client.PricingServiceClient;
import com.yourapp.company.dto.CompanyDto;
import com.yourapp.company.entity.Company;
import com.yourapp.company.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyService {

    private final CompanyRepository   companyRepository;
    private final AuthServiceClient   authServiceClient;
    private final PricingServiceClient pricingServiceClient;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_DATE_TIME;

    // ── Create company (registration flow) ───────────────────────

    /**
     * Full company registration in 3 steps:
     *  1. Save company to company_db
     *  2. Call Auth Service → create admin user
     *  3. Call Pricing Service → attach plan / start free trial
     */
    @Transactional
    public CompanyDto.CreateCompanyResponse createCompany(CompanyDto.CreateCompanyRequest req) {

        // Guard: company email must be unique
        if (companyRepository.existsByEmail(req.getCompanyEmail())) {
            throw new RuntimeException("A company with this email already exists");
        }

        // ── Step 1: save company ──────────────────────────────────
        Company company = Company.builder()
                .name(req.getCompanyName())
                .email(req.getCompanyEmail())
                .phone(req.getPhone())
                .address(req.getAddress())
                .industry(req.getIndustry())
                .status(Company.CompanyStatus.ACTIVE)
                .build();

        company = companyRepository.save(company);
        log.info("Company created: {} (id={})", company.getName(), company.getId());

        // ── Step 2: create admin user in Auth Service ─────────────
        CompanyDto.RegisterUserRequest userReq = CompanyDto.RegisterUserRequest.builder()
                .fullName(req.getAdminFullName())
                .email(req.getAdminEmail())
                .password(req.getAdminPassword())
                .companyId(company.getId())
                .role("ADMIN")
                .build();

        CompanyDto.AuthServiceResponse authResponse = authServiceClient.registerUser(userReq);

        if (!authResponse.isSuccess() || authResponse.getData() == null) {
            throw new RuntimeException("Failed to create admin user: " + authResponse.getMessage());
        }

        // Store the admin user ID on the company record
        company.setAdminUserId(authResponse.getData().getId());
        company = companyRepository.save(company);

        log.info("Admin user created (id={}) for company {}", authResponse.getData().getId(), company.getId());

        // ── Step 3: attach plan in Pricing Service ────────────────
        pricingServiceClient.createSubscription(company.getId(), req.getPlanId());

        // ── Build response ────────────────────────────────────────
        CompanyDto.AdminUserInfo adminInfo = CompanyDto.AdminUserInfo.builder()
                .id(authResponse.getData().getId())
                .email(authResponse.getData().getEmail())
                .fullName(authResponse.getData().getFullName())
                .role(authResponse.getData().getRole())
                .build();

        return CompanyDto.CreateCompanyResponse.builder()
                .company(toResponse(company))
                .adminUser(adminInfo)
                .message("Company registered successfully. Free trial started.")
                .build();
    }

    // ── Get company by ID ─────────────────────────────────────────

    public CompanyDto.CompanyResponse getCompany(Long id) {
        Company company = findById(id);
        return toResponse(company);
    }

    // ── Update company ────────────────────────────────────────────

    @Transactional
    public CompanyDto.CompanyResponse updateCompany(Long id, CompanyDto.UpdateCompanyRequest req,
                                                     Long requestingCompanyId) {
        Company company = findById(id);

        // A company can only update its own record
        if (!company.getId().equals(requestingCompanyId)) {
            throw new RuntimeException("You do not have permission to update this company");
        }

        if (req.getName()     != null) company.setName(req.getName());
        if (req.getPhone()    != null) company.setPhone(req.getPhone());
        if (req.getAddress()  != null) company.setAddress(req.getAddress());
        if (req.getIndustry() != null) company.setIndustry(req.getIndustry());
        if (req.getLogoUrl()  != null) company.setLogoUrl(req.getLogoUrl());

        company = companyRepository.save(company);
        log.info("Company updated: {}", company.getId());

        return toResponse(company);
    }

    // ── Suspend / delete (admin only) ─────────────────────────────

    @Transactional
    public void suspendCompany(Long id) {
        Company company = findById(id);
        company.setStatus(Company.CompanyStatus.SUSPENDED);
        companyRepository.save(company);
        log.info("Company suspended: {}", id);
    }

    // ── Helpers ───────────────────────────────────────────────────

    private Company findById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found: " + id));
    }

    private CompanyDto.CompanyResponse toResponse(Company c) {
        return CompanyDto.CompanyResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .email(c.getEmail())
                .phone(c.getPhone())
                .address(c.getAddress())
                .industry(c.getIndustry())
                .logoUrl(c.getLogoUrl())
                .status(c.getStatus().name())
                .adminUserId(c.getAdminUserId())
                .createdAt(c.getCreatedAt() != null ? c.getCreatedAt().format(FMT) : null)
                .build();
    }
}
