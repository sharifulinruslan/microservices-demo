package com.example.orderservice.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String password;
    private String role;
    private Integer year;
}
