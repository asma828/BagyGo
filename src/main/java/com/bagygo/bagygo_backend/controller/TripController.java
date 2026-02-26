package com.bagygo.bagygo_backend.controller;

import com.bagygo.bagygo_backend.dto.request.CreateTripRequest;
import com.bagygo.bagygo_backend.dto.response.TripResponse;
import com.bagygo.bagygo_backend.service.TripService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transporteur/trips")
public class TripController {

    private final TripService service;

    public TripController(TripService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasRole('TRANSPORTEUR')")
    public TripResponse create(
            @RequestBody CreateTripRequest request,
            Authentication authentication
    ) {
        return service.create(request, authentication.getName());
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('TRANSPORTEUR')")
    public List<TripResponse> getMyTrips(Authentication authentication) {
        return service.getMyTrips(authentication.getName());
    }
}

