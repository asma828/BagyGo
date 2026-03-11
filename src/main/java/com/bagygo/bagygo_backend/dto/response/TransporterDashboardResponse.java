package com.bagygo.bagygo_backend.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransporterDashboardResponse {
    private int activeTrips;
    private int offersMade;
    private int delivered;
    private double avgRating;
}