package com.yourapp.role_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionResponse {
    private Long id;

    private String resource;

    private boolean canCreate;

    private boolean canRead;

    private boolean canUpdate;

    private boolean canDelete;
}
