package com.yourapp.employee_service.service;

import com.yourapp.employee_service.dto.CreateEmployeeRequest;
import com.yourapp.employee_service.dto.EmployeeResponse;
import com.yourapp.employee_service.dto.PagedResponse;
import com.yourapp.employee_service.dto.UpdateEmployeeRequest;
import com.yourapp.employee_service.entity.Department;
import com.yourapp.employee_service.entity.Employee;
import com.yourapp.employee_service.repository.DepartmentRepository;
import com.yourapp.employee_service.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    public EmployeeResponse createEmployee(CreateEmployeeRequest request,Long companyId) {
        if(employeeRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("User already exists");
        }

        Employee employee = Employee.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .designation(request.getDesignation())
                .companyId(companyId)
                .teamId(request.getTeamId())
                .build();
        employee = employeeRepository.save(employee);
        if (request.getDepartmentId() != null) {
            Department dept = departmentRepository
                    .findByIdAndCompanyId(request.getDepartmentId(), companyId)
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            employee.setDepartment(dept);
            employee = employeeRepository.save(employee);
        }


        return toSingleResponse(employee);
    }

    public PagedResponse<EmployeeResponse> getEmployees(Long companyId, int page, int size, String sort, String search) {
        Page<Employee> employee;
        if(search != null && !search.isEmpty())
        {
            employee = employeeRepository.searchByCompanyId(companyId,search, PageRequest.of(page, size, Sort.by(sort).ascending()));
        }else{
            employee = employeeRepository.findByCompanyId(companyId,PageRequest.of(page, size, Sort.by(sort).ascending()));
        }

        return toResponse(employee);

    }

    public EmployeeResponse getEmployee(Long employeeId, Long companyId) {
       Employee employee= employeeRepository.findByIdAndCompanyId(employeeId, companyId).orElseThrow(() -> new RuntimeException("Employee not found"));
       return toSingleResponse(employee);

    }

    public EmployeeResponse updateEmployee(Long employeeId, UpdateEmployeeRequest request, Long companyId) {
        Employee employee = employeeRepository.findByIdAndCompanyId(employeeId, companyId).orElseThrow(() -> new RuntimeException("Employee not found"));
        if (request.getFirstName()  != null) employee.setFirstName(request.getFirstName());
        if (request.getLastName()   != null) employee.setLastName(request.getLastName());
        if (request.getPhone()      != null) employee.setPhone(request.getPhone());
        if (request.getDesignation()!= null) employee.setDesignation(request.getDesignation());
        if (request.getTeamId()     != null) employee.setTeamId(request.getTeamId());
        if (request.getDepartmentId() != null) {
           Department department = departmentRepository.findByIdAndCompanyId(
                            request.getDepartmentId(), companyId)
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            employee.setDepartment(department);
        }

        employee = employeeRepository.save(employee);

        return toSingleResponse(employee);
    }

    public EmployeeResponse toggleStatus(Long id, Long companyId) {
        Employee employee = employeeRepository.findByIdAndCompanyId(id, companyId).orElseThrow(() -> new RuntimeException("Employee not found"));
        if(employee.getStatus() == Employee.EmployeeStatus.ACTIVE) {
            employee.setStatus(Employee.EmployeeStatus.INACTIVE);
        }else{
            employee.setStatus(Employee.EmployeeStatus.ACTIVE);
        }
        employee = employeeRepository.save(employee);
        return toSingleResponse(employee);
    }

    public void deleteEmployee(Long employeeId, Long companyId) {
        Employee employee = employeeRepository.findByIdAndCompanyId(employeeId, companyId).orElseThrow(() -> new RuntimeException("Employee not found"));
        employeeRepository.delete(employee);
    }



    private PagedResponse<EmployeeResponse> toResponse(Page<Employee> employee) {
        PagedResponse<EmployeeResponse> response = PagedResponse.<EmployeeResponse>builder()
                .content(employee.stream().map(this::toSingleResponse).toList())
                .pageNumber(employee.getNumber())
                .pageSize(employee.getSize())
                .totalElements(employee.getTotalElements())
                .totalPages(employee.getTotalPages())
                .last(employee.isLast())
                .build();

        return response;

    }

    private EmployeeResponse toSingleResponse(Employee employee) {
        EmployeeResponse employeeResponse = EmployeeResponse.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .phone(employee.getPhone())
                .designation(employee.getDesignation())
                .departmentName(employee.getDepartment() != null ? employee.getDepartment().getName() : null)
                .departmentId(employee.getDepartment() != null ? employee.getDepartment().getId() : null)
                .teamId(employee.getTeamId())
                .companyId(employee.getCompanyId())
                .status(employee.getStatus().name())
                .createdAt(employee.getCreatedAt()!=null ? employee.getCreatedAt().toString():null)
                .build();

        return employeeResponse;
    }

}
