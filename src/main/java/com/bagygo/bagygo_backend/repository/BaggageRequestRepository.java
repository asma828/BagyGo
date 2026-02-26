package com.bagygo.bagygo_backend.repository;

import com.bagygo.bagygo_backend.entity.BaggageRequest;
import com.bagygo.bagygo_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BaggageRequestRepository
        extends JpaRepository<BaggageRequest, Long> {

    List<BaggageRequest> findByUserId(Long userId);
    List<BaggageRequest> findByUser(User user);

}

