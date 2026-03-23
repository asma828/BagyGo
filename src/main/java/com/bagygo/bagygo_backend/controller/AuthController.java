package com.bagygo.bagygo_backend.controller;

import com.bagygo.bagygo_backend.dto.request.LoginRequest;
import com.bagygo.bagygo_backend.dto.request.RegisterRequest;
import com.bagygo.bagygo_backend.dto.response.AuthResponse;
import com.bagygo.bagygo_backend.dto.response.UserResponse;
import com.bagygo.bagygo_backend.entity.User;
import com.bagygo.bagygo_backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.ok(authService.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    /**
     * Returns the current authenticated user's profile.
     * Used by the frontend on app reload to re-hydrate user state.
     * GET /api/auth/me (requires Bearer token)
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(
            @AuthenticationPrincipal(expression = "user") User user) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(UserResponse.from(user));
    }
}