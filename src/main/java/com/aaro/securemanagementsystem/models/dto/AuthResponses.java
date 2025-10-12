package com.aaro.securemanagementsystem.models.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponses {
    private int statusCode;
    private String message;
    private String token;
    private String refreshToken;
    private String role;
    private String expirationTime;
}
