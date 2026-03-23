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
    private int pendingPayments;
    private double avgRating;
    private List<BaggageRequestResponse> activeRequestsList;
    private List<TripResponse> availableTrips;
    private List<ActivityItemResponse> recentActivity;
}