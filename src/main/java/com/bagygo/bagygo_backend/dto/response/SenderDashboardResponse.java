package com.bagygo.bagygo_backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SenderDashboardResponse {
    private int activeRequests;
    private int offersReceived;
    private int delivered;
    private double avgRating;
    private List<BaggageRequestResponse> activeRequestsList;
    private List<ActivityItemResponse> recentActivity;
}