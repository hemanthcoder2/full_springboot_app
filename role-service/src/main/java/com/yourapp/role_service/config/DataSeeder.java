package com.yourapp.role_service.config;

import com.yourapp.role_service.entity.Permission;
import com.yourapp.role_service.entity.Role;
import com.yourapp.role_service.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        if (roleRepository.count() == 0) {

            // ADMIN — full access to everything
            roleRepository.save(Role.builder()
                    .name("Admin")
                    .description("Full access to all services")
                    .companyId(0L) // 0 = system-level default
                    .defaultRole(true)
                    .permissions(new ArrayList<>(List.of(
                            buildPermission("EMPLOYEES", true, true, true, true),
                            buildPermission("TEAMS", true, true, true, true),
                            buildPermission("ROLES", true, true, true, true),
                            buildPermission("DASHBOARD", true, true, true, true)
                    )))
                    .build());

            // HR — full access to employees, teams, roles. No dashboard delete
            roleRepository.save(Role.builder()
                    .name("HR")
                    .description("Access to employees, teams and role management")
                    .companyId(0L)
                    .defaultRole(true)
                    .permissions(new ArrayList<>(List.of(
                            buildPermission("EMPLOYEES", true, true, true, true),
                            buildPermission("TEAMS", true, true, true, true),
                            buildPermission("ROLES", true, true, true, true),
                            buildPermission("DASHBOARD", false, true, false, false)
                    )))
                    .build());

            // EMPLOYEE — can only read their own employee record
            roleRepository.save(Role.builder()
                    .name("Employee")
                    .description("Can only view own employee record")
                    .companyId(0L)
                    .defaultRole(true)
                    .permissions(new ArrayList<>(List.of(
                            buildPermission("EMPLOYEES", false, true, false, false),
                            buildPermission("TEAMS", false, true, false, false),
                            buildPermission("ROLES", false, false, false, false),
                            buildPermission("DASHBOARD", false, true, false, false)
                    )))
                    .build());

            log.info("Default roles seeded successfully");
        }
    }

    private Permission buildPermission(String resource,
                                       boolean canCreate,
                                       boolean canRead,
                                       boolean canUpdate,
                                       boolean canDelete) {
        return Permission.builder()
                .resource(resource)
                .canCreate(canCreate)
                .canRead(canRead)
                .canUpdate(canUpdate)
                .canDelete(canDelete)
                .build();
    }
}