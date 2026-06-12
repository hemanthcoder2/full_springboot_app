package com.yourapp.dashboard_service.repository;

import com.yourapp.dashboard_service.entity.Dashboard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DashboardRepository extends JpaRepository<Dashboard, Long> {
    // All dashboards for a company (paginated)
    Page<Dashboard> findByCompanyId(Long companyId, Pageable pageable);

    // Security check — dashboard must belong to company
    Optional<Dashboard> findByIdAndCompanyId(Long id, Long companyId);

    // Search by title
    @Query("SELECT d FROM Dashboard d WHERE d.companyId = :companyId AND " +
            "LOWER(d.title) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Dashboard> searchByCompanyId(@Param("companyId") Long companyId,
                                      @Param("search") String search,
                                      Pageable pageable);

    // Filter by status — e.g. get only PUBLISHED dashboards
    Page<Dashboard> findByCompanyIdAndStatus(Long companyId,
                                             Dashboard.DashboardStatus status,
                                             Pageable pageable);
}
