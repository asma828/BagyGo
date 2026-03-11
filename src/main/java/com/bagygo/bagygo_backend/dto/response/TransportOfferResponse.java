package com.bagygo.bagygo_backend.dto.response;

import com.bagygo.bagygo_backend.entity.TransportOffer;
import com.bagygo.bagygo_backend.enums.TransportOfferStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransportOfferResponse {
    private Long id;
    private Long baggageRequestId;
    private UserResponse transporter;
    private Double proposedPrice;
    private String message;
    private TransportOfferStatus status;
    private LocalDateTime createdAt;

    public static TransportOfferResponse from(TransportOffer o) {
        TransportOfferResponse r = new TransportOfferResponse();
        r.setId(o.getId());
        r.setBaggageRequestId(o.getBaggageRequest().getId());
        r.setTransporter(UserResponse.from(o.getUser()));
        r.setProposedPrice(o.getProposedPrice());
        r.setStatus(o.getStatus());
        r.setCreatedAt(o.getCreatedAt());
        return r;
    }
}