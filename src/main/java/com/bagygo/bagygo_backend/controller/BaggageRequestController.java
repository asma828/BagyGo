package com.bagygo.bagygo_backend.controller;

import com.bagygo.bagygo_backend.dto.request.CreateBaggageRequestRequest;
import com.bagygo.bagygo_backend.dto.response.BaggageRequestResponse;
import com.bagygo.bagygo_backend.entity.User;
import com.bagygo.bagygo_backend.security.CustomUserDetails;
import com.bagygo.bagygo_backend.service.BaggageRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class BaggageRequestController {

    private final BaggageRequestService service;

    @PostMapping
    public ResponseEntity<BaggageRequestResponse> create(
            @Valid @RequestBody CreateBaggageRequestRequest req,
            @AuthenticationPrincipal CustomUserDetails principal) {

        User sender = principal.getUser();
        return ResponseEntity.ok(service.create(req, sender));
    }

    @GetMapping("/my")
    public ResponseEntity<List<BaggageRequestResponse>> getMine(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(service.getMySenderRequests(user));
    }

    @GetMapping("/open")
    public ResponseEntity<List<BaggageRequestResponse>> getOpen() {
        return ResponseEntity.ok(service.getOpenRequests());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaggageRequestResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<BaggageRequestResponse> cancel(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(service.cancel(id, user));
    }
}