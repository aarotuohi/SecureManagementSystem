package com.aaro.securemanagementsystem.models.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthRequests {
    @Data
    public static class Login {
        @Email @NotBlank
        private String email;
        @NotBlank @Size(min = 6, max = 128)
        private String password;
    }

    @Data
    public static class Register {
        @NotBlank
        private String name;
        @Email @NotBlank
        private String email;
        @NotBlank @Size(min = 6, max = 128)
        private String password;
        private String city;
        @NotBlank
        private String role; // "ADMIN" | "USER"
    }
}
