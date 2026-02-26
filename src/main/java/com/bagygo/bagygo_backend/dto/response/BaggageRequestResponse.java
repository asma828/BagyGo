package com.bagygo.bagygo_backend.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BaggageRequestResponse {

    private Long id;
    private String description;
    private Double weight;
    private Double proposedPrice;
    private String status;
    private LocalDateTime createdAt;

}

