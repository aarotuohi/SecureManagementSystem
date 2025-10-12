package com.aaro.securemanagementsystem.models.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private Integer id;
    private String email;
    private String name;
    private String city;
    private String role;
}
