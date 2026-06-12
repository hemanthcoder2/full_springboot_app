package com.yourapp.teams_service.service;

import com.yourapp.teams_service.dto.CreateTeamRequest;
import com.yourapp.teams_service.dto.PagedResponse;
import com.yourapp.teams_service.dto.TeamResponse;
import com.yourapp.teams_service.dto.UpdateTeamRequest;
import com.yourapp.teams_service.entity.Team;
import com.yourapp.teams_service.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamService {

    private final TeamRepository teamRepository;

    public TeamResponse createTeam(CreateTeamRequest request, Long companyId) {

        log.info("Creating Team Request: {}" ,request.getTeamName());
        Team team = Team.builder()
                .teamName(request.getTeamName())
                .teamDescription(request.getTeamDescription())
                .companyId(companyId)
                .build();

        if(request.getParentId()!=null){
             Team parent = teamRepository.findByIdAndCompanyId(request.getParentId(), companyId).orElseThrow(()->new RuntimeException("Parent Team Not Found"));
             team.setParent(parent);
        }

        team = teamRepository.save(team);
        log.info("Created Team Request: {}" ,team.getTeamName());
        return toResponse(team);

    }

    public PagedResponse<TeamResponse> getTeams(Long companyId,int page,int size,String sort, String search) {


        Page<Team> team;
        if(search==null||search.isEmpty()){
            log.info("Getting Teams for Company Id: {}" ,companyId);
            team = teamRepository.findByCompanyId(companyId, PageRequest.of(page,size, Sort.by(sort).ascending()));
        }else{
            log.info("Getting Teams for Company Id: {} with {}" ,companyId,search);
            team = teamRepository.searchByCompanyId(companyId,search,PageRequest.of(page,size, Sort.by(sort).ascending()));
        }


        return pagedresponse(team);
    }

    public TeamResponse getTeamByIdAndCompanyId(Long id,Long companyId){
        log.info("Getting Team Response for Team Id: {} in company: {}" ,id,companyId);
        Team team = teamRepository.findByIdAndCompanyId(id,companyId).orElseThrow(()->new RuntimeException("Team Not Found"));
        return toResponse(team);
    }

    public TeamResponse updateTeam(Long id, UpdateTeamRequest request, Long companyId){
        log.info("Updating Team Request: {} with companyId: {}" ,id,companyId);
        Team team = teamRepository.findByIdAndCompanyId(id,companyId).orElseThrow(()->new RuntimeException("Team Not Found"));
        if(request.getTeamName()!=null){team.setTeamName(request.getTeamName());}
        if(request.getTeamDescription()!=null){team.setTeamDescription(request.getTeamDescription());}

        team = teamRepository.save(team);
        log.info("Updated Team Request: {} with Team Name: {}" ,id,team.getTeamName());
        return toResponse(team);
    }

    public TeamResponse addMember(Long teamId,Long employeeId,Long companyId){

        log.info("Adding member:{} into Team: {}",employeeId,teamId);
        Team team = teamRepository.findByIdAndCompanyId(teamId,companyId).orElseThrow(()->new RuntimeException("Team Not Found"));

        if(teamRepository.existsMember(teamId,employeeId)){
            throw new RuntimeException("Employee in Team Already Exists");
        }

        team.getMemberIds().add(employeeId);

        team = teamRepository.save(team);
        log.info("Added Member:{} into Team: {}",employeeId,teamId);
        return toResponse(team);

    }

    public TeamResponse removeMember(Long teamId,Long employeeId,Long companyId){
        Team team = teamRepository.findByIdAndCompanyId(teamId,companyId).orElseThrow(()->new RuntimeException("Team Not Found"));

        if(!teamRepository.existsMember(teamId,employeeId)){
            throw new RuntimeException("Employee in Team Not Found");
        }

        team.getMemberIds().remove((Object) employeeId);

        team = teamRepository.save(team);
        log.info("Removed Member:{} from Team: {}",employeeId,teamId);

        return toResponse(team);

    }

    public TeamResponse toggleStatus(Long teamId,Long companyId){
        Team team = teamRepository.findByIdAndCompanyId(teamId,companyId).orElseThrow(()->new RuntimeException("Team Not Found"));
        if(team.getStatus().name().equals("ACTIVE")){

            team.setStatus(Team.TeamStatus.INACTIVE);

        }else{
            team.setStatus(Team.TeamStatus.ACTIVE);
        }

        team = teamRepository.save(team);
        log.info("Team Status:{} in Team: {}",team.getStatus(),teamId);
        return toResponse(team);
    }


    private PagedResponse<TeamResponse> pagedresponse(Page<Team> team) {
        PagedResponse<TeamResponse> response = PagedResponse.<TeamResponse> builder()
                .content(team.stream().map(this::toResponse).toList())
                .pageNumber(team.getNumber())
                .pageSize(team.getSize())
                .totalElements(team.getTotalElements())
                .totalPages(team.getTotalPages())
                .last(team.isLast())
                .build();

        return response;

    }

    private TeamResponse toResponse(Team team) {
        TeamResponse teamResponse = TeamResponse.builder()
                .id(team.getId())
                .teamName(team.getTeamName())
                .teamDescription(team.getTeamDescription())
                .companyId(team.getCompanyId())
                .parentId(team.getParent() != null ? team.getParent().getId() : null)
                .parentName(team.getParent() != null ? team.getParent().getTeamName() : null)
                .memberIds(team.getMemberIds())
                .memberCount(team.getMemberIds().size())
                .childCount(team.getChildren().size())
                .status(team.getStatus().name())
                .createdAt(team.getCreatedAt()!=null?team.getCreatedAt().toString():null)
                .build();

        return teamResponse;
    }
}
