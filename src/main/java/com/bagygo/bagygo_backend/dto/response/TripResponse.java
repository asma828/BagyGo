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
    private LocalDateTime createdAt;

    public static TripResponse from(Trip t) {
        TripResponse r = new TripResponse();
        r.setId(t.getId());
        r.setTransporter(UserResponse.from(t.getTransporter()));
        r.setDepartureCity(t.getDepartureCity());
        r.setArrivalCity(t.getArrivalCity());
        r.setDepartureDate(t.getDepartureDate());
        r.setEstimatedArrival(t.getEstimatedArrival());
        r.setAvailableSpace(t.getAvailableSpace());
        r.setPricePerKg(t.getPricePerKg());
        r.setStatus(t.getStatus());
        r.setNotes(t.getNotes());
        r.setCreatedAt(t.getCreatedAt());
        return r;
    }
}