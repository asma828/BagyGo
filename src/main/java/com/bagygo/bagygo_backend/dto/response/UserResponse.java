package com.bagygo.bagygo_backend.dto.response;

import com.bagygo.bagygo_backend.entity.User;
import com.bagygo.bagygo_backend.enums.UserRole;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private UserRole role;
    private Double rating;
    private Integer totalDeliveries;
    private String avatarUrl;
    private LocalDateTime createdAt;

    public static UserResponse from(User u) {
        UserResponse r = new UserResponse();
        r.setId(u.getId());
        r.setFirstName(u.getFirstName());
        r.setLastName(u.getLastName());
        r.setEmail(u.getEmail());
        r.setPhone(u.getPhone());
        r.setRole(u.getRole());
        r.setRating(u.getRating());
        r.setTotalDeliveries(u.getTotalDeliveries());
        r.setAvatarUrl(u.getAvatarUrl());
        r.setCreatedAt(u.getCreatedAt());
        return r;
    }
}