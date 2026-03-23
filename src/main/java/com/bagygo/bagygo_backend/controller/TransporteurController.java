package com.bagygo.bagygo_backend.controller;

import com.bagygo.bagygo_backend.dto.request.CreateTransportOfferRequest;
import com.bagygo.bagygo_backend.dto.response.TransportOfferResponse;
import com.bagygo.bagygo_backend.entity.User;
import com.bagygo.bagygo_backend.service.TransporteurOfferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
public class TransporteurController {

    private final TransporteurOfferService service;

    @PostMapping
    public ResponseEntity<TransportOfferResponse> makeOffer(
            @Valid @RequestBody CreateTransportOfferRequest req,
            @AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(service.createOffer(req, user.getEmail()));
    }

    @GetMapping("/request/{requestId}")
    public ResponseEntity<List<TransportOfferResponse>> getOffersForRequest(@PathVariable("requestId") Long requestId) {
        return ResponseEntity.ok(service.getOffersForRequest(requestId));
    }

    @GetMapping("/my")
    public ResponseEntity<List<TransportOfferResponse>> getMyOffers(Authentication authentication) {
        // We need to pass the user object to getMyOffers, let's update service to take
        // email or find user there
        return ResponseEntity.ok(service.getMyOffersByEmail(authentication.getName()));
    }

    @PatchMapping("/{id}/accept")
    public ResponseEntity<?> accept(
            @PathVariable("id") Long id,
            Authentication authentication) {

        service.transporterAcceptOffer(id, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/decline")
    public ResponseEntity<?> decline(
            @PathVariable("id") Long id,
            Authentication authentication) {

        service.transporterRejectOffer(id, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/counter")
    public ResponseEntity<TransportOfferResponse> counter(
            @PathVariable("id") Long id,
            @RequestBody Map<String, Double> body,
            Authentication authentication) {
        return ResponseEntity.ok(service.counterOfferByEmail(id, body.get("proposedPrice"), authentication.getName()));
    }

    @PatchMapping("/requests/{requestId}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable("requestId") Long requestId,
            @RequestBody Map<String, String> body,
            Authentication authentication) {
        String statusStr = body.get("status");
        com.bagygo.bagygo_backend.enums.RequestStatus status = com.bagygo.bagygo_backend.enums.RequestStatus
                .valueOf(statusStr);
        service.updateStatus(requestId, status, authentication.getName());
        return ResponseEntity.ok().build();
    }
}