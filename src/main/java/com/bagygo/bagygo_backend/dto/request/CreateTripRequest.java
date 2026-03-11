        package com.bagygo.bagygo_backend.dto.request;

import jakarta.validation.constraints.*;
        import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateTripRequest {
    @NotBlank  private String departureCity;
    @NotBlank  private String arrivalCity;
    @NotNull   @Future  private LocalDateTime departureDate;
    private LocalDateTime estimatedArrival;
    @NotNull   @Positive private Double availableSpace;
    @NotNull   @Positive private Double pricePerKg;
    private String notes;
}