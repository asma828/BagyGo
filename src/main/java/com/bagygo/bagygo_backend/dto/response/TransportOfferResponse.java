package com.bagygo.bagygo_backend.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransportOfferResponse {

    private Long id;
    private Double proposedPrice;
    private String status;
    private String transporteurName;
}

