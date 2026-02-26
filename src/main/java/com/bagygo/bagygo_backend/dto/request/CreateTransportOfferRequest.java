package com.bagygo.bagygo_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateTransportOfferRequest {

    private Long baggageRequestId;
    private Long tripId;
    private Double proposedPrice;

}
