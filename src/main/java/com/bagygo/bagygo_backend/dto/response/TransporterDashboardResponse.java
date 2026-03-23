package com.bagygo.bagygo_backend.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class TransporterDashboardResponse {
    private int activeTrips;
    private int offersMade;
    private int delivered;
    private double avgRating;
    private List<ActivityItemResponse> recentActivity;
}