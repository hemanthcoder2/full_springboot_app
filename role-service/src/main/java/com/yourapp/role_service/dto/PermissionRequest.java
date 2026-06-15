package com.yourapp.role_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionRequest {

    @NotBlank(message = "Resource is required")
    private String resource;

    private boolean canCreate;

    private boolean canRead;

    private boolean canUpdate;

    private boolean canDelete;

}
