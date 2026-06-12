package com.yourapp.teams_service.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamResponse {
    private Long id;
    private String teamName;
    private String teamDescription;
    private Long companyId;
    private Long parentId;
    private String parentName;
    private List<Long> memberIds;
    private int memberCount;
    private int childCount;
    private String status;
    private String createdAt;
}
