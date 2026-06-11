package com.yourapp.employee_service.repository;


import com.yourapp.employee_service.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee,Long> {
    boolean existsByEmail(String email);

    Page<Employee> findByCompanyId(Long companyId, Pageable pageable);

    Optional<Employee> findByIdAndCompanyId(Long id, Long companyId);

    @Query("SELECT e FROM Employee e WHERE e.companyId = :companyId AND " +
            "(LOWER(e.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(e.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Employee> searchByCompanyId(@Param("companyId") Long companyId,
                                     @Param("search") String search,
                                     Pageable pageable);

}
