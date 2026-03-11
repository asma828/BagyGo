package com.bagygo.bagygo_backend.service;

import com.bagygo.bagygo_backend.dto.response.ActivityItemResponse;
import com.bagygo.bagygo_backend.dto.response.BaggageRequestResponse;
import com.bagygo.bagygo_backend.dto.response.SenderDashboardResponse;
import com.bagygo.bagygo_backend.dto.response.TransporterDashboardResponse;
import com.bagygo.bagygo_backend.entity.User;
import com.bagygo.bagygo_backend.enums.RequestStatus;
import com.bagygo.bagygo_backend.enums.TripStatus;
import com.bagygo.bagygo_backend.repository.BaggageRequestRepository;
import com.bagygo.bagygo_backend.repository.TransportOfferRepository;
import com.bagygo.bagygo_backend.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final BaggageRequestRepository requestRepo;
    private final TransportOfferRepository offerRepo;
    private final TripRepository tripRepo;

    public SenderDashboardResponse getSenderDashboard(User user) {
        long active    = requestRepo.countByUserAndStatusNotIn(user,
                RequestStatus.DELIVERED, RequestStatus.CANCELLED);
        long offers    = offerRepo.countByRequest_User(user);
        long delivered = requestRepo.countBySenderAndStatus(user, RequestStatus.DELIVERED);

        // Get active requests
        List<BaggageRequestResponse> activeRequests = requestRepo
                .findBySenderOrderByCreatedAtDesc(user)
                .stream()
                .map(BaggageRequestResponse::from)
                .toList();

        // Example recent activity: last 10 offers across all requests
        List<ActivityItemResponse> recentActivity = activeRequests.stream()
                .flatMap(req -> req.getOffers().stream())
                .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt())) // newest first
                .limit(10)
                .map(offer -> new ActivityItemResponse(
                        offer.getId(),
                        "NEW_OFFER",
                        "New offer received",
                        offer.getProposedPrice() + " MAD from " + offer.getUser().getFirstName(),
                        offer.getCreatedAt()
                ))
                .toList();

        return SenderDashboardResponse.builder()
                .activeRequests((int) active)
                .offersReceived((int) offers)
                .delivered((int) delivered)
                .avgRating(user.getRating())
                .activeRequestsList(activeRequests)
                .recentActivity(recentActivity)
                .build();
    }

    public TransporterDashboardResponse getTransporterDashboard(User user) {
        long activeTrips = tripRepo.countByTransporterAndStatusNotIn(
                user,
                List.of(TripStatus.COMPLETED, TripStatus.CANCELLED)
        );
        long offersMade  = offerRepo.countByUser(user);
        long delivered   = offerRepo.countByUserAndStatus(user,
                com.bagygo.bagygo_backend.enums.TransportOfferStatus.ACCEPTED);

        return TransporterDashboardResponse.builder()
                .activeTrips((int) activeTrips)
                .offersMade((int) offersMade)
                .delivered((int) delivered)
                .avgRating(user.getRating())
                .build();
    }
}