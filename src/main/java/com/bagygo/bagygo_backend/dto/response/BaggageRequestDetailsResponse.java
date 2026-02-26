package com.bagygo.bagygo_backend.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaggageRequestDetailsResponse {

    private Long id;
    private String description;
    private Double weight;
    private Double proposedPrice;
    private String status;
    private LocalDateTime createdAt;
    private List<TransportOfferResponse> offers;

}

