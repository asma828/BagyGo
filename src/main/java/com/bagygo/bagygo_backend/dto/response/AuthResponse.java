package com.bagygo.bagygo_backend.dto.response;

import com.bagygo.bagygo_backend.enums.UserRole;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String token;
    private String email;
    private String firstName;
    private String lastName;
    private UserRole role;
    private String message;
}