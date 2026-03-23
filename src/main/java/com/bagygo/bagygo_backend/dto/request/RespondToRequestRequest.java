package com.bagygo.bagygo_backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RespondToRequestRequest {
    @NotNull
    @FutureOrPresent
    private LocalDateTime departureDate;
    
    private LocalDateTime estimatedArrival;
    
    @NotNull
    @Positive
    private Double pricePerKg;
    
    private String notes;
}
