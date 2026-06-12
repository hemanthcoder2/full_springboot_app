package com.yourapp.teams_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTeamRequest {

    private String teamName;
    private String teamDescription;
}
