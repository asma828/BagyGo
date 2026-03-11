package com.bagygo.bagygo_backend.controller;

import com.bagygo.bagygo_backend.dto.response.SenderDashboardResponse;
import com.bagygo.bagygo_backend.dto.response.TransporterDashboardResponse;
import com.bagygo.bagygo_backend.entity.User;
import com.bagygo.bagygo_backend.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/me/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/sender")
    public ResponseEntity<SenderDashboardResponse> senderDashboard(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(dashboardService.getSenderDashboard(user));
    }

    @GetMapping("/transporter")
    public ResponseEntity<TransporterDashboardResponse> transporterDashboard(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(dashboardService.getTransporterDashboard(user));
    }
}