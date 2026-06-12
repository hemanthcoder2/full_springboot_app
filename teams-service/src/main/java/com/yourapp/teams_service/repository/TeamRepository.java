package com.yourapp.teams_service.repository;

import com.yourapp.teams_service.entity.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    Page<Team> findByCompanyId(Long companyId, Pageable pageable);

    Optional<Team> findByIdAndCompanyId(Long id, Long companyId);

    Page<Team> findByCompanyIdAndParentIsNull(Long companyId, Pageable pageable);

    @Query("SELECT t from Team t WHERE t.companyId = :companyId AND " +
    "LOWER(t.teamName) LIKE LOWER(CONCAT('%',:search, '%'))")
    Page<Team> searchByCompanyId(@Param("companyId") Long companyId,@Param("search") String search, Pageable pageable);


    @Query("SELECT COUNT(m) > 0 FROM Team t JOIN t.memberIds m " +
            "WHERE t.id = :teamId AND m = :employeeId")
    boolean existsMember(@Param("teamId") Long teamId, @Param("employeeId") Long employeeId);
}
