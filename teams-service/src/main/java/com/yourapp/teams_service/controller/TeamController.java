package com.yourapp.teams_service.controller;

import com.yourapp.teams_service.dto.*;
import com.yourapp.teams_service.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/teams")
public class TeamController {
    private final TeamService teamService;

    @PostMapping
    public ResponseEntity<ApiResponse> createTeam(@Valid @RequestBody CreateTeamRequest request, @RequestHeader("X-Company-Id") Long companyId) {
        TeamResponse response = teamService.createTeam(request, companyId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Team created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getTeams(@RequestHeader("X-Company-Id") Long companyId
            ,@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "teamName") String sort
    , @RequestParam(required = false) String search){

        PagedResponse<TeamResponse> response = teamService.getTeams(companyId, page, size, sort, search);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Teams found successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getTeamByIdAndCompanyId(@PathVariable Long id,@RequestHeader("X-Company-Id") Long companyId){
        TeamResponse response = teamService.getTeamByIdAndCompanyId(id, companyId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Team found successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateTeam(@PathVariable Long id, @Valid @RequestBody UpdateTeamRequest request, @RequestHeader("X-Company-Id") Long companyId) {
        TeamResponse response = teamService.updateTeam(id, request, companyId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Team updated successfully", response));
    }

    @PostMapping("/{id}/members/{employeeId}")
    public ResponseEntity<ApiResponse> addMembers(@PathVariable Long id,@PathVariable Long employeeId,@RequestHeader("X-Company-Id") Long companyId){
        TeamResponse response = teamService.addMember(id, employeeId, companyId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Team member added successfully", response));
    }


    @DeleteMapping("/{id}/members/{employeeId}")
    public ResponseEntity<ApiResponse> removeMember(@PathVariable Long id,@PathVariable Long employeeId,@RequestHeader("X-Company-Id") Long companyId){
        TeamResponse response = teamService.removeMember(id, employeeId, companyId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Team member removed successfully", response));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse> toggleStatus(@PathVariable Long id,@RequestHeader("X-Company-Id") Long companyId){
        TeamResponse response = teamService.toggleStatus(id, companyId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Team status changed successfully", response));
    }




}
