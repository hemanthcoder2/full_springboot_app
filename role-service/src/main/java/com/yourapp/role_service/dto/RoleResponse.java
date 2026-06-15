package com.yourapp.role_service.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleResponse {

    private Long id;

    private String name;

    private String description;

    private Long companyId;

    private boolean defaultRole;

    private List<PermissionResponse> permissions;

    private String createdAt;
}
