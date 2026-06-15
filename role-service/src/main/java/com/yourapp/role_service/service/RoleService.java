package com.yourapp.role_service.service;

import com.yourapp.role_service.dto.*;
import com.yourapp.role_service.entity.Permission;
import com.yourapp.role_service.entity.Role;
import com.yourapp.role_service.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService {
    private final RoleRepository roleRepository;

    public List<RoleResponse> getRoles(Long companyId){

        List<Role> roles = roleRepository.findByCompanyId(companyId);
        return toResponse(roles);
    }

    public RoleResponse getRole(Long id, Long companyId){
        Role role = roleRepository.findByIdAndCompanyId(id,companyId).orElseThrow(
                ()->new RuntimeException("Role Not found")
        );

        return toSingleResponse(role);
    }

    public RoleResponse createRole(CreateRoleRequest req, Long companyId){
        if(roleRepository.existsByNameAndCompanyId(req.getName(),companyId)){
            throw new RuntimeException("Role already Exists");
        }

        Role role = Role.builder()
                .name(req.getName())
                .description(req.getDescription())
                .companyId(companyId)
                .defaultRole(false)
                .build();

        role = roleRepository.save(role);
        return toSingleResponse(role);
    }

    public RoleResponse updateRole(Long id, UpdateRoleRequest req, Long companyId){
        Role role = roleRepository.findByIdAndCompanyId(id,companyId).orElseThrow(
                ()->new RuntimeException("Role Not found")
        );
        if(role.isDefaultRole()){
            throw new RuntimeException("Cannot edit default role");
        }

        if(req.getName()!=null) role.setName(req.getName());

        if(req.getDescription()!=null) role.setDescription(req.getDescription());

        role = roleRepository.save(role);
        return toSingleResponse(role);
    }
    public void deleteRole(Long id, Long companyId){
        Role role = roleRepository.findByIdAndCompanyId(id,companyId).orElseThrow(

                ()->new RuntimeException("Role not found")
        );

        if(role.isDefaultRole()){
            throw new RuntimeException("Cannot delete a default role");
        }

        roleRepository.delete(role);
    }

    @Transactional
    public RoleResponse duplicateRole(Long id, Long companyId){
        Role role = roleRepository.findByIdAndCompanyId(id,companyId).orElseThrow(

                ()->new RuntimeException("Role not found")
        );
        Role duplicateRole = Role.builder()
                .name(role.getName()+" Copy")
                .description(role.getDescription())
                .permissions(new ArrayList<>(role.getPermissions().stream()
                        .map(p -> Permission.builder()
                                .resource(p.getResource())
                                .canCreate(p.isCanCreate())
                                .canRead(p.isCanRead())
                                .canUpdate(p.isCanUpdate())
                                .canDelete(p.isCanDelete())
                                .build())
                        .toList()))
                .defaultRole(false)
                .companyId(companyId)
                .build();

        duplicateRole = roleRepository.save(duplicateRole);

        return toSingleResponse(duplicateRole);
    }
    @Transactional
    public RoleResponse setPermissions(Long id, List<PermissionRequest> requests, Long companyId){
        Role role = roleRepository.findByIdAndCompanyId(id,companyId).orElseThrow(

                ()->new RuntimeException("Role not found")
        );

        role.getPermissions().clear();

        List<Permission> newPermissions = new ArrayList<>(requests.stream()
                .map(req -> Permission.builder()
                        .resource(req.getResource())
                        .canCreate(req.isCanCreate())
                        .canRead(req.isCanRead())
                        .canUpdate(req.isCanUpdate())
                        .canDelete(req.isCanDelete())
                        .build())
                .toList());
        role.getPermissions().addAll(newPermissions);

        role = roleRepository.save(role);
        return toSingleResponse(role);
    }




    private List<RoleResponse> toResponse(List<Role> roles) {
        List<RoleResponse> response = roles.stream()
                .map(this::toSingleResponse).toList();

        return response;
    }

    private RoleResponse toSingleResponse(Role role) {

        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .companyId(role.getCompanyId())
                .defaultRole(role.isDefaultRole())
                .permissions(role.getPermissions().stream().map(this::permissionResponse).toList())
                .createdAt(role.getCreatedAt()!=null?role.getCreatedAt().toString():null)
                .build();

    }

    private PermissionResponse permissionResponse(Permission permission) {
        return PermissionResponse.builder()
                .id(permission.getId())
                .resource(permission.getResource())
                .canCreate(permission.isCanCreate())
                .canRead(permission.isCanRead())
                .canUpdate(permission.isCanUpdate())
                .canDelete(permission.isCanDelete())
                .build();
    }
}
