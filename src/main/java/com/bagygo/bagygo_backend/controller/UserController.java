package com.bagygo.bagygo_backend.controller;

import com.bagygo.bagygo_backend.dto.request.UpdateProfileRequest;
import com.bagygo.bagygo_backend.entity.User;
import com.bagygo.bagygo_backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Get current user info
    @GetMapping("/me")
    public ResponseEntity<User> getMe(Authentication auth) {
        User user = userService.getCurrentUser(auth.getName());
        return ResponseEntity.ok(user);
    }

    // Update profile
    @PatchMapping("/me")
    public ResponseEntity<User> updateProfile(
            @Valid @RequestBody UpdateProfileRequest req,
            Authentication auth) {

        User updated = userService.updateProfile(auth.getName(), req);
        return ResponseEntity.ok(updated);
    }
}