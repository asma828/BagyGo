package com.bagygo.bagygo_backend.dto.request;

import lombok.*;

@Data
public class LoginRequest {
    private String email;
    private String password;
}

