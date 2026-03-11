package com.bagygo.bagygo_backend.repository;

import com.bagygo.bagygo_backend.entity.BaggageRequest;
import com.bagygo.bagygo_backend.entity.TransportOffer;
import com.bagygo.bagygo_backend.entity.User;
import com.bagygo.bagygo_backend.enums.TransportOfferStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransportOfferRepository extends JpaRepository<TransportOffer, Long> {

    List<TransportOffer> findByBaggageRequestOrderByCreatedAtDesc(BaggageRequest request);

    List<TransportOffer> findByUserOrderByCreatedAtDesc(User user);

    boolean existsByBaggageRequestAndUser(BaggageRequest request, User user);

    List<TransportOffer> findByBaggageRequestAndStatus(BaggageRequest request, TransportOfferStatus status);

    long countByUserAndStatus(User user, TransportOfferStatus status);

    // Count all offers for requests belonging to a sender
    long countByBaggageRequest_Sender(User sender);
    // Alias for DashboardService
    default long countByRequest_User(User user) {
        return countByBaggageRequest_Sender(user);
    }

    long countByUser(User user);
}