package com.bagygo.bagygo_backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateBaggageRequestRequest {
    @NotBlank
    private String departureCity;
    @NotBlank
    private String arrivalCity;
    @NotNull
    @FutureOrPresent
    private LocalDate desiredDate;
    @NotNull
    @Positive
    private Double weightKg;
    @NotBlank
    private String description;
    @NotNull
    @Positive
    private Double proposedPrice;
    private Boolean isFragile = false;
    private Long tripId;
    private String message;
}