package com.bagygo.bagygo_backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateBaggageRequestRequest {

    @NotBlank
    private String description;

    @NotNull
    private Double weight;

    @NotNull
    private Double proposedPrice;

    @NotBlank
    private String departureCity;

    @NotBlank
    private String arrivalCity;
}

