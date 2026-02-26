package com.bagygo.bagygo_backend.controller;

import com.bagygo.bagygo_backend.dto.request.CreateTransportOfferRequest;
import com.bagygo.bagygo_backend.dto.response.TransportOfferResponse;
import com.bagygo.bagygo_backend.service.BaggageRequestService;
import com.bagygo.bagygo_backend.service.TransporteurOfferService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/Transporteur")
public class TransporteurController {


    private final TransporteurOfferService transportOfferService;

    public TransporteurController(TransporteurOfferService transportOfferService) {
        this.transportOfferService = transportOfferService;
    }

    @PostMapping("/offers")
    @PreAuthorize("hasRole('TRANSPORTEUR')")
    public TransportOfferResponse createOffer(
            @RequestBody CreateTransportOfferRequest request,
            Authentication authentication
    ) {
        return transportOfferService.createOffer(
                request,
                authentication.getName()
        );
    }

}
