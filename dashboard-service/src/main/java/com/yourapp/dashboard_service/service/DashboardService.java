package com.yourapp.dashboard_service.service;

import com.yourapp.dashboard_service.dto.CreateDashboardRequest;
import com.yourapp.dashboard_service.dto.DashboardResponse;
import com.yourapp.dashboard_service.entity.Dashboard;
import com.yourapp.dashboard_service.repository.DashboardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {
    private final DashboardRepository dashboardRepository;
    public DashboardResponse createDashboard(CreateDashboardRequest request,Long companyId, Long userId) {
        Dashboard dashboard = dashboardRepository.findBy
    }
}
