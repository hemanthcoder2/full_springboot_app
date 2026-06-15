package com.yourapp.dashboard_service.controller;

import com.yourapp.dashboard_service.dto.*;
import com.yourapp.dashboard_service.service.DashboardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dashboards")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @PostMapping
    public ResponseEntity<ApiResponse> createDashboard(
            @Valid @RequestBody CreateDashboardRequest request,
            @RequestHeader("X-Company-Id") Long companyId,
            @RequestHeader("X-User-Id") Long userId){
        DashboardResponse response = dashboardService.createDashboard(request,companyId,userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse
                .success("Dashboard Created Successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getDashboards(
            @RequestHeader("X-Company-Id") Long companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sort,
            @RequestParam(required = false) String search){

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse
                .success("Getting Dashboards",
                        dashboardService.getDashboards(companyId,page,size,sort,search)));

    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getDashboard(@PathVariable Long id,
            @RequestHeader("X-Company-Id") Long companyId){
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse
                .success("Dashboard with id and company id",
                        dashboardService.getDashboard(id,companyId)));
    }


    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateDashboard(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDashboardRequest request,
            @RequestHeader("X-Company-Id") Long companyId){
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse
                .success("Dashboard updated with id and company id",
                        dashboardService.updateDashboard(id,request,companyId)));
    }

    @PatchMapping("/{id}/publish")
    public ResponseEntity<ApiResponse> publishDashboard(
            @PathVariable Long id,
            @RequestHeader("X-Company-Id") Long companyId
    ){
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse
                .success("Dashboard updated with id and company id",
                        dashboardService.publishDashboard(id,companyId)));
    }

    @PatchMapping("/{id}/archive")
    public ResponseEntity<ApiResponse> archiveDashboard(
            @PathVariable Long id,
            @RequestHeader("X-Company-Id") Long companyId
    ){
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse
                .success("Dashboard updated with id and company id",
                        dashboardService.archiveDashboard(id,companyId)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteDashboard(
            @PathVariable Long id,
            @RequestHeader("X-Company-Id") Long companyId) {
        dashboardService.deleteDashboard(id, companyId);
        return ResponseEntity.ok(ApiResponse.success("Dashboard deleted successfully", null));
    }




}
