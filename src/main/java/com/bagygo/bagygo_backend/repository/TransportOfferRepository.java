package com.bagygo.bagygo_backend.repository;

import com.bagygo.bagygo_backend.entity.BaggageRequest;
import com.bagygo.bagygo_backend.entity.TransportOffer;
import com.bagygo.bagygo_backend.entity.User;
import com.bagygo.bagygo_backend.enums.TransportOfferStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransportOfferRepository extends JpaRepository<TransportOffer, Long> {

    List<TransportOffer> findByBaggageRequestOrderByCreatedAtDesc(@Param("request") BaggageRequest request);

    List<TransportOffer> findByUserOrderByCreatedAtDesc(@Param("user") User user);

    boolean existsByBaggageRequestAndUser(@Param("request") BaggageRequest request, @Param("user") User user);

    List<TransportOffer> findByBaggageRequestAndStatus(@Param("request") BaggageRequest request,
            @Param("status") TransportOfferStatus status);

    long countByUserAndStatus(@Param("user") User user, @Param("status") TransportOfferStatus status);

    // Count all offers for requests belonging to a sender
    long countByBaggageRequest_Sender(@Param("sender") User sender);

    // Alias for DashboardService
    default long countByRequest_User(User user) {
        return countByBaggageRequest_Sender(user);
    }

    long countByUser(@Param("user") User user);

    long countByUserAndStatusIn(@Param("user") User user, @Param("statuses") List<TransportOfferStatus> statuses);
}