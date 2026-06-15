package com.yourapp.role_service.controller;

import com.yourapp.role_service.dto.*;
import com.yourapp.role_service.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/roles")
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<ApiResponse> getRoles(
            @RequestHeader("X-Company-Id") Long companyId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Roles fetched", roleService.getRoles(companyId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getRole(
            @PathVariable Long id,
            @RequestHeader("X-Company-Id") Long companyId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Role fetched", roleService.getRole(id, companyId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createRole(
            @Valid @RequestBody CreateRoleRequest request,
            @RequestHeader("X-Company-Id") Long companyId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                "Role created", roleService.createRole(request, companyId)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRoleRequest request,
            @RequestHeader("X-Company-Id") Long companyId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Role updated", roleService.updateRole(id, request, companyId)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteRole(
            @PathVariable Long id,
            @RequestHeader("X-Company-Id") Long companyId) {
        roleService.deleteRole(id, companyId);
        return ResponseEntity.ok(ApiResponse.success("Role deleted", null));
    }

    @PostMapping("/{id}/duplicate")
    public ResponseEntity<ApiResponse> duplicateRole(
            @PathVariable Long id,
            @RequestHeader("X-Company-Id") Long companyId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                "Role duplicated", roleService.duplicateRole(id, companyId)));
    }

    @PutMapping("/{id}/permissions")
    public ResponseEntity<ApiResponse> setPermissions(
            @PathVariable Long id,
            @RequestBody List<PermissionRequest> requests,
            @RequestHeader("X-Company-Id") Long companyId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Permissions updated", roleService.setPermissions(id, requests, companyId)));
    }
}