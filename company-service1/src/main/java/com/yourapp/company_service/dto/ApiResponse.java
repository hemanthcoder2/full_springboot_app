package com.yourapp.company_service.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse {
    private boolean success;
    private String message;
    private Object data;

    public static ApiResponse success(String message, Object data) {

        ApiResponse response = ApiResponse.builder()
                .success(true).message(message).data(data).build();
        return response;

    }

    public static ApiResponse error(String message) {
        ApiResponse response = ApiResponse.builder()
                .success(false).message(message).build();
        return response;
    }
}
