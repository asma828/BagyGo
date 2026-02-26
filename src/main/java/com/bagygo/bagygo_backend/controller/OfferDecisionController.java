package com.bagygo.bagygo_backend.controller;

import com.bagygo.bagygo_backend.service.TransporteurOfferService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/expediteur/offers")
public class OfferDecisionController {

    private final TransporteurOfferService service;

    public OfferDecisionController(TransporteurOfferService service) {
        this.service = service;
    }

    @PostMapping("/{id}/accept")
    @PreAuthorize("hasRole('EXPEDITEUR')")
    public void acceptOffer(
            @PathVariable Long id,
            Authentication authentication
    ) {
        service.acceptOffer(id, authentication.getName());
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('EXPEDITEUR')")
    public void rejectOffer(
            @PathVariable Long id,
            Authentication authentication
    ) {
        service.rejectOffer(id, authentication.getName());
    }
}
