package com.yourapp.employee_service.service;

import com.yourapp.employee_service.dto.CreateDepartmentRequest;
import com.yourapp.employee_service.dto.DepartmentResponse;
import com.yourapp.employee_service.entity.Department;
import com.yourapp.employee_service.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    public DepartmentResponse createDepartment(CreateDepartmentRequest request,Long companyId) {
        if(departmentRepository.existsByNameAndCompanyId(request.getName(), companyId)){
            throw new RuntimeException("Department already exists");
        }

        Department department = Department.builder()
                .name(request.getName())
                .description(request.getDescription())
                .companyId(companyId)
                .build();

        department = departmentRepository.save(department);

        return toSingleResponse(department);

    }

    public List<DepartmentResponse> getDepartmentsByCompany(Long companyId) {
        List<Department> departments= departmentRepository.findByCompanyId(companyId);
        return toResponse(departments);
    }

    public void deleteDepartment(Long id, Long companyId) {
        Department department = departmentRepository.findByIdAndCompanyId(id,companyId).orElseThrow(() -> new RuntimeException("Department not found"));
        departmentRepository.delete(department);
    }

    private List<DepartmentResponse> toResponse(List<Department> departments){
        return departments.stream()
                .map(this::toSingleResponse)
                .toList();

    }

    private DepartmentResponse toSingleResponse(Department department) {
        DepartmentResponse departmentResponse = DepartmentResponse.builder()
                .id(department.getId())
                .name(department.getName())
                .description(department.getDescription())
                .companyId(department.getCompanyId())
                .createdAt(department.getCreatedAt() != null ? department.getCreatedAt().toString() : null)
                .build();
        return departmentResponse;
    }
}
