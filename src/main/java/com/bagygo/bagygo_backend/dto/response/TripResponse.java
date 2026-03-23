package com.bagygo.bagygo_backend.dto.response;

import com.bagygo.bagygo_backend.entity.Trip;
import com.bagygo.bagygo_backend.enums.TripStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TripResponse {
    private Long id;
    private UserResponse transporter;
    private String departureCity;
    private String arrivalCity;
    private LocalDateTime departureDate;
    private LocalDateTime estimatedArrival;
    private Double availableSpace;
    private Double pricePerKg;
    private TripStatus status;
    private String notes;
    private Double currentLat;
    private Double currentLng;
    private LocalDateTime createdAt;

    public static TripResponse from(Trip t) {
        if (t == null)
            return null;
        TripResponse r = new TripResponse();
        r.setId(t.getId());
        r.setTransporter(t.getTransporter() != null ? UserResponse.from(t.getTransporter()) : null);
        r.setDepartureCity(t.getDepartureCity());
        r.setArrivalCity(t.getArrivalCity());
        r.setDepartureDate(t.getDepartureDate());
        r.setEstimatedArrival(t.getEstimatedArrival());
        r.setAvailableSpace(t.getAvailableSpace());
        r.setPricePerKg(t.getPricePerKg());
        r.setStatus(t.getStatus());
        r.setNotes(t.getNotes());
        r.setCurrentLat(t.getCurrentLat());
        r.setCurrentLng(t.getCurrentLng());
        r.setCreatedAt(t.getCreatedAt());
        return r;
    }
}