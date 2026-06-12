package com.yourapp.teams_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse {

    private boolean success;
    private String message;
    private Object data;

    public static ApiResponse success(String message, Object data) {

        ApiResponse apiResponse = ApiResponse.builder()
                .success(true)
                .message(message)
                .data(data)
                .build();

        return apiResponse;
    }

    public static ApiResponse error(String message) {
        ApiResponse apiResponse = ApiResponse.builder()
                .success(false)
                .message(message)
                .build();

        return apiResponse;
    }

}
