package com.bagygo.bagygo_backend.controller;

import com.bagygo.bagygo_backend.dto.response.SenderDashboardResponse;
import com.bagygo.bagygo_backend.dto.response.TransporterDashboardResponse;
import com.bagygo.bagygo_backend.entity.User;
import com.bagygo.bagygo_backend.service.DashboardService;
import com.bagygo.bagygo_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/me/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final UserService userService;

    @GetMapping("/sender")
    public ResponseEntity<SenderDashboardResponse> senderDashboard(Authentication auth) {
        System.out.println("DEBUG DashboardController: Received request for /sender");
        if (auth == null) {
            System.err.println("DEBUG DashboardController: Authentication is NULL!");
            throw new RuntimeException("Authentication is null");
        }
        System.out.println("DEBUG DashboardController: Auth Name: " + auth.getName());
        
        User user = userService.getCurrentUser(auth.getName());
        if (user == null) {
            System.err.println("DEBUG DashboardController: User entity is NULL for email: " + auth.getName());
            throw new RuntimeException("User not found: " + auth.getName());
        }
        
        System.out.println("DEBUG DashboardController: User found: " + user.getEmail() + ", Role: " + user.getRole());
        
        if (Boolean.TRUE.equals(user.getIsBanned())) {
            throw new RuntimeException("Your account has been banned by the admin.");
        }
        return ResponseEntity.ok(dashboardService.getSenderDashboard(user));
    }

    @GetMapping("/transporter")
    public ResponseEntity<TransporterDashboardResponse> transporterDashboard(Authentication auth) {
        User user = userService.getCurrentUser(auth.getName());
        if (Boolean.TRUE.equals(user.getIsBanned())) {
            throw new RuntimeException("Your account has been banned by the admin.");
        }
        return ResponseEntity.ok(dashboardService.getTransporterDashboard(user));
    }
}