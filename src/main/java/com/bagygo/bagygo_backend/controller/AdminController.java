package com.bagygo.bagygo_backend.controller;

import com.bagygo.bagygo_backend.entity.User;
import com.bagygo.bagygo_backend.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // ── User Management ───────────────────────────────────
    @GetMapping("/users")
    public ResponseEntity<Page<User>> getAllUsers(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(adminService.getAllUsers(pageable));
    }

    @GetMapping("/users/search")
    public ResponseEntity<Page<User>> searchUsers(
            @RequestParam("query") String query,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(adminService.searchUsers(query, pageable));
    }

    @PatchMapping("/users/{id}/ban")
    public ResponseEntity<?> updateBanStatus(@PathVariable("id") Long id, @RequestParam("banned") boolean banned) {
        adminService.updateUserStatus(id, banned);
        return ResponseEntity.ok().build();
    }

    // ── Transporter Verification ──────────────────────────
    @GetMapping("/verifications/pending")
    public ResponseEntity<Page<User>> getPendingVerifications(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(adminService.getPendingVerifications(pageable));
    }

    @PatchMapping("/verifications/{id}/approve")
    public ResponseEntity<?> verifyTransporter(@PathVariable("id") Long id, @RequestParam("approve") boolean approve) {
        adminService.verifyTransporter(id, approve);
        return ResponseEntity.ok().build();
    }

    // ── Monitoring ────────────────────────────────────────
    @GetMapping("/trips")
    public ResponseEntity<Page<com.bagygo.bagygo_backend.dto.response.TripResponse>> getAllTrips(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(adminService.getAllTrips(pageable));
    }

    @GetMapping("/requests")
    public ResponseEntity<Page<com.bagygo.bagygo_backend.dto.response.BaggageRequestResponse>> getAllRequests(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(adminService.getAllRequests(pageable));
    }

    @GetMapping("/payments")
    public ResponseEntity<Page<com.bagygo.bagygo_backend.dto.response.PaymentResponse>> getAllPayments(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(adminService.getAllPayments(pageable));
    }

    // ── Statistics ───────────────────────────────────────
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(adminService.getPlatformStats());
    }
}
