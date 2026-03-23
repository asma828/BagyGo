package com.bagygo.bagygo_backend.dto.response;

import com.bagygo.bagygo_backend.entity.BaggageRequest;
import com.bagygo.bagygo_backend.enums.RequestStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BaggageRequestResponse {
    private Long id;
    private UserResponse sender;
    private String departureCity;
    private String arrivalCity;
    private LocalDate desiredDate;
    private Double weightKg;
    private String description;
    private Double proposedPrice;
    private RequestStatus status;
    private Boolean isFragile;
    private Boolean isPaid;
    private Boolean isDedicatedTrip;
    private String imageUrl;
    private int offersCount;
    private TripResponse trip;
    private String message;
    private LocalDateTime createdAt;

    public static BaggageRequestResponse from(BaggageRequest br) {
        BaggageRequestResponse r = new BaggageRequestResponse();
        r.setId(br.getId());
        r.setSender(UserResponse.from(br.getSender()));
        r.setDepartureCity(br.getDepartureCity());
        r.setArrivalCity(br.getArrivalCity());
        r.setDesiredDate(br.getDesiredDate());
        r.setWeightKg(br.getWeightKg());
        r.setDescription(br.getDescription());
        r.setProposedPrice(br.getProposedPrice());
        r.setStatus(br.getStatus());
        r.setIsFragile(br.getIsFragile());
        r.setIsPaid(br.getIsPaid() != null ? br.getIsPaid() : false);
        r.setIsDedicatedTrip(br.getIsDedicatedTrip() != null ? br.getIsDedicatedTrip() : false);
        r.setImageUrl(br.getImageUrl());
        r.setOffersCount(br.getOffers() != null ? br.getOffers().size() : 0);
        r.setCreatedAt(br.getCreatedAt());
        r.setMessage(br.getMessage());
        if (br.getTrip() != null) {
            r.setTrip(TripResponse.from(br.getTrip()));
        }
        return r;
    }
}