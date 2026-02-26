package com.bagygo.bagygo_backend.repository;

import com.bagygo.bagygo_backend.entity.TransportOffer;
import com.bagygo.bagygo_backend.entity.BaggageRequest;
import com.bagygo.bagygo_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransportOfferRepository extends JpaRepository<TransportOffer, Long> {

    List<TransportOffer> findByUser(User user);
    List<TransportOffer> findByBaggageRequest(BaggageRequest request);

}

