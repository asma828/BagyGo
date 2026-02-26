package com.bagygo.bagygo_backend.controller;

import com.bagygo.bagygo_backend.dto.request.CreateBaggageRequestRequest;
import com.bagygo.bagygo_backend.dto.response.BaggageRequestDetailsResponse;
import com.bagygo.bagygo_backend.dto.response.BaggageRequestResponse;
import com.bagygo.bagygo_backend.service.BaggageRequestService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/baggage-requests")
public class BaggageRequestController {

    private final BaggageRequestService service;

    public BaggageRequestController(BaggageRequestService service) {
        this.service = service;
    }

    @PreAuthorize("hasRole('EXPEDITEUR')")
    @PostMapping
    public ResponseEntity<BaggageRequestResponse> create(
            @Valid @RequestBody CreateBaggageRequestRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();
        return ResponseEntity.ok(service.create(request, email));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('EXPEDITEUR')")
    public List<BaggageRequestResponse> getMyRequests(Authentication authentication) {

        String email = authentication.getName();
        return service.getMyRequests(email);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('EXPEDITEUR')")
    public BaggageRequestDetailsResponse getDetails(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return service.getDetails(id, authentication.getName());
    }

}

