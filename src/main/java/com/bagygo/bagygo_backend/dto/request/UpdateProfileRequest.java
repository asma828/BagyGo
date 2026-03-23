package com.bagygo.bagygo_backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateProfileRequest {


    @Size(min = 2, max = 60)
    private String firstName;

    @Size(min = 2, max = 60)
    private String lastName;

    @Size(min = 6, max = 20)
    private String phone;

    private String avatarUrl;
}