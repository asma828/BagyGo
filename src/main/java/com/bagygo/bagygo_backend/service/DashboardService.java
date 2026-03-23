package com.bagygo.bagygo_backend.service;

import com.bagygo.bagygo_backend.dto.response.ActivityItemResponse;
import com.bagygo.bagygo_backend.dto.response.BaggageRequestResponse;
import com.bagygo.bagygo_backend.dto.response.SenderDashboardResponse;
import com.bagygo.bagygo_backend.dto.response.TransporterDashboardResponse;
import com.bagygo.bagygo_backend.dto.response.TripResponse;
import com.bagygo.bagygo_backend.entity.BaggageRequest;
import com.bagygo.bagygo_backend.entity.User;
import com.bagygo.bagygo_backend.enums.RequestStatus;
import com.bagygo.bagygo_backend.enums.TripStatus;
import com.bagygo.bagygo_backend.repository.BaggageRequestRepository;
import com.bagygo.bagygo_backend.repository.TransportOfferRepository;
import com.bagygo.bagygo_backend.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Transactional
public class DashboardService {

        private final BaggageRequestRepository requestRepo;
        private final TransportOfferRepository offerRepo;
        private final TripRepository tripRepo;

        public SenderDashboardResponse getSenderDashboard(User user) {
                try {
                        long active = requestRepo.countBySenderAndStatusIn(user, 
                                List.of(RequestStatus.OPEN, RequestStatus.PENDING, RequestStatus.ACCEPTED, RequestStatus.IN_TRANSIT));
                        long offers = offerRepo.countByBaggageRequest_Sender(user);
                        long delivered = requestRepo.countBySenderAndStatusIn(user, 
                                List.of(RequestStatus.DELIVERED, RequestStatus.COMPLETED));
                        long pendingPayments = requestRepo.countBySenderAndStatusAndIsPaidFalse(user, RequestStatus.ACCEPTED);

                        // Get active requests entities
                        List<BaggageRequest> activeEntities = requestRepo
                                        .findBySenderOrderByCreatedAtDesc(user);

                        List<BaggageRequestResponse> activeRequests = activeEntities
                                        .stream()
                                        .map(BaggageRequestResponse::from)
                                        .toList();

                        // Available trips: Use specialized repository method
                        List<TripResponse> availableTrips = tripRepo.findAvailableTripsForDashboard()
                                        .stream()
                                        .limit(20)
                                        .map(TripResponse::from)
                                        .toList();

                        // Recent activity
                        List<ActivityItemResponse> recentActivity = new ArrayList<>();
                        try {
                            // Last 5 offers received
                            activeEntities.stream()
                                .filter(req -> req.getOffers() != null)
                                .flatMap(req -> req.getOffers().stream())
                                .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
                                .limit(5)
                                .forEach(offer -> recentActivity.add(new ActivityItemResponse(
                                    offer.getId(), "NEW_OFFER", "New offer", 
                                    offer.getProposedPrice() + " MAD from " + offer.getUser().getFirstName(),
                                    offer.getCreatedAt())));
                        } catch (Exception e) {}

                        return SenderDashboardResponse.builder()
                                        .activeRequests((int) active)
                                        .offersReceived((int) offers)
                                        .delivered((int) delivered)
                                        .pendingPayments((int) pendingPayments)
                                        .avgRating(user.getRating() != null ? user.getRating() : 0.0)
                                        .activeRequestsList(activeRequests)
                                        .availableTrips(availableTrips)
                                        .recentActivity(recentActivity)
                                        .build();
                } catch (Exception e) {
                        try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter("c:/laragon/www/bagygo-backend/dashboard_error.txt", true))) {
                            pw.println("--- ERROR AT " + java.time.LocalDateTime.now() + " ---");
                            e.printStackTrace(pw);
                        } catch (java.io.IOException ioEx) {
                            ioEx.printStackTrace();
                        }
                        throw new RuntimeException("Dashboard processing error: " + e.getMessage());
                }
        }

        public TransporterDashboardResponse getTransporterDashboard(User user) {
                long activeTrips = tripRepo.countByTransporterAndStatusNotIn(
                                user,
                                List.of(TripStatus.COMPLETED, TripStatus.CANCELLED));
                long requestsReceived = requestRepo.countByTrip_Transporter(user);
                long delivered = requestRepo.countByTransporterAndStatusIn(user,
                                List.of(RequestStatus.DELIVERED, RequestStatus.COMPLETED));

                return TransporterDashboardResponse.builder()
                                .activeTrips((int) activeTrips)
                                .offersMade((int) requestsReceived)
                                .delivered((int) delivered)
                                .avgRating(user.getRating() != null ? user.getRating() : 0.0)
                                .recentActivity(List.of())
                                .build();
        }
}
