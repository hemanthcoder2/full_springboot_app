package com.yourapp.company_service.repository;

import com.yourapp.company_service.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company,Long> {
    boolean existsByEmail(String email);
    Optional<Company> findByEmail(String email);

}
