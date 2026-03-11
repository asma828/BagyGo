package com.bagygo.bagygo_backend.repository;

import com.bagygo.bagygo_backend.entity.Trip;
import com.bagygo.bagygo_backend.entity.User;
import com.bagygo.bagygo_backend.enums.TripStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    // Trips for a user, ordered by departure date
    List<Trip> findByTransporterOrderByDepartureDateDesc(User transporter);

    // Trips with a specific status
    List<Trip> findByStatusOrderByDepartureDateAsc(TripStatus status);

    // Trips by departure/arrival city and status
    List<Trip> findByDepartureCityAndArrivalCityAndStatusOrderByDepartureDateAsc(
            String departureCity,
            String arrivalCity,
            TripStatus status
    );

    // Count trips for a user with a specific status
    long countByTransporterAndStatus(User transporter, TripStatus status);

    // Count trips for a user excluding certain statuses
    long countByTransporterAndStatusNotIn(User transporter, List<TripStatus> statuses);

}