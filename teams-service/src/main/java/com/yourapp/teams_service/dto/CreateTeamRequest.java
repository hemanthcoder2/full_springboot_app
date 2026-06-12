package com.yourapp.teams_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTeamRequest {

    @NotBlank(message = "Team Name is Required")
    private String teamName;
    private String teamDescription;
    private Long parentId;
}
