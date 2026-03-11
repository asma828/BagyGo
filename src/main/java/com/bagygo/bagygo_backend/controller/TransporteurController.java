package com.bagygo.bagygo_backend.controller;

import com.bagygo.bagygo_backend.dto.request.CreateTransportOfferRequest;
import com.bagygo.bagygo_backend.dto.response.TransportOfferResponse;
import com.bagygo.bagygo_backend.entity.User;
import com.bagygo.bagygo_backend.service.TransporteurOfferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(service.createOffer(req, user.getEmail()));
    }

    @GetMapping("/request/{requestId}")
    public ResponseEntity<List<TransportOfferResponse>> getOffersForRequest(@PathVariable Long requestId) {
        return ResponseEntity.ok(service.getOffersForRequest(requestId));
    }

    @GetMapping("/my")
    public ResponseEntity<List<TransportOfferResponse>> getMyOffers(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(service.getMyOffers(user));
    }

    @PatchMapping("/{id}/accept")
    public ResponseEntity<?> accept(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        service.acceptOffer(id, user.getEmail());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/decline")
    public ResponseEntity<?> decline(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        service.rejectOffer(id, user.getEmail());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/counter")
    public ResponseEntity<TransportOfferResponse> counter(
            @PathVariable Long id,
            @RequestBody Map<String, Double> body,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(service.counterOffer(id, body.get("proposedPrice"), user));
    }
}