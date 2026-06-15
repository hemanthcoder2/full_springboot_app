package com.yourapp.dashboard_service.service;

import com.yourapp.dashboard_service.dto.CreateDashboardRequest;
import com.yourapp.dashboard_service.dto.DashboardResponse;
import com.yourapp.dashboard_service.dto.PagedResponse;
import com.yourapp.dashboard_service.dto.UpdateDashboardRequest;
import com.yourapp.dashboard_service.entity.Dashboard;
import com.yourapp.dashboard_service.repository.DashboardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {
    private final DashboardRepository dashboardRepository;
    public DashboardResponse createDashboard(CreateDashboardRequest request,Long companyId, Long userId) {
        log.info("Creating Dashboard : {} and CompanyId: {}",request.getTitle(), companyId);
        Dashboard dashboard = Dashboard.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .companyId(companyId)
                .createdBy(userId)
                .build();
        dashboard = dashboardRepository.save(dashboard);
        log.info("Created Dashboard : {} and CompanyId: {}",request.getTitle(), companyId);
        return toSingleResponse(dashboard);
    }

    public PagedResponse<DashboardResponse> getDashboards(Long companyId, int page, int size, String sort, String search){

        Page<Dashboard> dashboard;
        if(search == null || search.isEmpty()){
            dashboard = dashboardRepository.findByCompanyId
                    (companyId, PageRequest.of(page, size, Sort.by(sort).ascending()));

        }else{
            dashboard = dashboardRepository.searchByCompanyId(
                    companyId, search, PageRequest.of(page, size, Sort.by(sort).ascending()));
        }

        return pagedresponse(dashboard);

    }

    public DashboardResponse getDashboard(Long id, Long companyId){
        return toSingleResponse(dashboardRepository.findByIdAndCompanyId(id,companyId)
                .orElseThrow(() -> new RuntimeException("Dashboard not found")));
    }

    public DashboardResponse updateDashboard(Long id, UpdateDashboardRequest request, Long companyId){
        Dashboard dashboard = dashboardRepository.findByIdAndCompanyId(id,companyId)
                .orElseThrow(() -> new RuntimeException("Dashboard not found"));

        if(request.getTitle()!=null) dashboard.setTitle(request.getTitle());
        if(request.getDescription()!=null) dashboard.setDescription(request.getDescription());

        dashboard = dashboardRepository.save(dashboard);

        return toSingleResponse(dashboard);
    }

    public DashboardResponse publishDashboard(Long id, Long companyId){
        Dashboard dashboard = dashboardRepository.findByIdAndCompanyId(id,companyId)
                .orElseThrow(() -> new RuntimeException("Dashboard not found"));

        if(dashboard.getStatus() == Dashboard.DashboardStatus.ARCHIVED){
            throw new RuntimeException("Cannot publish Archived Dashboard");
        }

        dashboard.setStatus(Dashboard.DashboardStatus.PUBLISHED);
        dashboard = dashboardRepository.save(dashboard);
        return toSingleResponse(dashboard);
    }
    public DashboardResponse archiveDashboard(Long id, Long companyId){
        Dashboard dashboard = dashboardRepository.findByIdAndCompanyId(id,companyId)
                .orElseThrow(() -> new RuntimeException("Dashboard not found"));

        if(dashboard.getStatus() == Dashboard.DashboardStatus.DRAFT){
            throw new RuntimeException("Cannot archive draft Dashboard");
        }

        dashboard.setStatus(Dashboard.DashboardStatus.ARCHIVED);
        dashboard = dashboardRepository.save(dashboard);
        return toSingleResponse(dashboard);
    }

    public void deleteDashboard(Long id, Long companyId){
        Dashboard dashboard = dashboardRepository.findByIdAndCompanyId(id,companyId)
                .orElseThrow(() -> new RuntimeException("Dashboard not found"));
        dashboardRepository.delete(dashboard);
    }

    private PagedResponse<DashboardResponse> pagedresponse(Page<Dashboard> dashboard) {
        return PagedResponse.<DashboardResponse> builder()
                .content(dashboard.stream().map(this::toSingleResponse).toList())
                .pageNumber(dashboard.getNumber())
                .pageSize(dashboard.getSize())
                .totalElements(dashboard.getTotalElements())
                .totalPages(dashboard.getTotalPages())
                .last(dashboard.isLast())
                .build();
    }

    private DashboardResponse toSingleResponse(Dashboard dashboard) {
        return DashboardResponse.builder()
                .id(dashboard.getId())
                .title(dashboard.getTitle())
                .description(dashboard.getDescription())
                .companyId(dashboard.getCompanyId())
                .status(dashboard.getStatus().name())
                .createdBy(dashboard.getCreatedBy())
                .createdAt(dashboard.getCreatedAt()!=null ? dashboard.getCreatedAt().toString() : null)
                .build();
    }
}
