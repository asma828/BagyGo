package com.bagygo.bagygo_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateTripRequest {

    private String departureCity;
    private String arrivalCity;
    private LocalDateTime departureDate;
    private Double availableWeight;


}
