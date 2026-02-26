package com.bagygo.bagygo_backend.repository;

import com.bagygo.bagygo_backend.entity.Trip;
import com.bagygo.bagygo_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByUser(User user);

}
