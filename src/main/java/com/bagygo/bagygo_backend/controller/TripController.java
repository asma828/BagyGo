package com.bagygo.bagygo_backend.controller;

import com.bagygo.bagygo_backend.dto.request.CreateTripRequest;
import com.bagygo.bagygo_backend.dto.response.TripResponse;
import com.bagygo.bagygo_backend.service.TripService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    private final TripService service;

    public TripController(TripService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasRole('TRANSPORTEUR')")
    public TripResponse create(
            @RequestBody CreateTripRequest request,
            Authentication authentication) {
        return service.create(request, authentication.getName());
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('TRANSPORTEUR')")
    public List<TripResponse> getMyTrips(Authentication authentication) {
        return service.getMyTrips(authentication.getName());
    }

    @GetMapping("/search")
    public List<TripResponse> search(
            @RequestParam("departureCity") String departureCity,
            @RequestParam("arrivalCity") String arrivalCity,
            @RequestParam("date") String date,
            @RequestParam("weight") Double weight) {
        try {
            String trimmedDate = date.trim();

            java.time.LocalDateTime ldt;
            if (trimmedDate.length() == 10) {
                ldt = java.time.LocalDate.parse(trimmedDate).atStartOfDay();
            } else {
                ldt = java.time.LocalDateTime.parse(trimmedDate);
            }

            return service.searchTrips(departureCity, arrivalCity, ldt, weight);
        } catch (Exception e) {
            System.err.println("Search failed for parameters: departureCity=" + departureCity +
                    ", arrivalCity=" + arrivalCity + ", date=" + date + ", weight=" + weight);
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @PutMapping("/{id}/location")
    @PreAuthorize("hasRole('TRANSPORTEUR')")
    public TripResponse updateLocation(
            @PathVariable("id") Long id,
            @RequestParam("lat") Double lat,
            @RequestParam("lng") Double lng,
            Authentication authentication) {
        return service.updateLocation(id, lat, lng, authentication.getName());
    }

    @GetMapping("/{id}")
    public TripResponse getById(@PathVariable("id") Long id) {
        return service.getById(id);
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('TRANSPORTEUR')")
    public TripResponse cancel(@PathVariable("id") Long id, Authentication authentication) {
        return service.cancel(id, authentication.getName());
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('TRANSPORTEUR')")
    public TripResponse updateStatus(
            @PathVariable("id") Long id,
            @RequestParam("status") String status,
            Authentication authentication) {
        return service.updateStatus(id, com.bagygo.bagygo_backend.enums.TripStatus.valueOf(status), authentication.getName());
    }

    @GetMapping("/{id}/location")
    public TripResponse getLocation(@PathVariable("id") Long id) {
        return service.getById(id);
    }
}
