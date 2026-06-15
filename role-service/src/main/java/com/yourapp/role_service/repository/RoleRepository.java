package com.yourapp.role_service.repository;

import com.yourapp.role_service.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {

    List<Role> findByCompanyId(Long companyId);

    Optional<Role> findByIdAndCompanyId(Long id, Long companyId);

    boolean existsByNameAndCompanyId(String name, Long companyId);

    List<Role> findByDefaultRoleTrue();
}
