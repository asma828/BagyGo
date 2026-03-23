package com.bagygo.bagygo_backend.repository;

import com.bagygo.bagygo_backend.entity.Trip;
import com.bagygo.bagygo_backend.entity.User;
import com.bagygo.bagygo_backend.enums.TripStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    // Trips for a user, ordered by departure date
    List<Trip> findByTransporterOrderByDepartureDateDesc(@Param("transporter") User transporter);

    // Trips with a specific status
    List<Trip> findByStatusOrderByDepartureDateAsc(@Param("status") TripStatus status);

    // Trips by departure/arrival city and status
    List<Trip> findByDepartureCityAndArrivalCityAndStatusOrderByDepartureDateAsc(
            @Param("departureCity") String departureCity,
            @Param("arrivalCity") String arrivalCity,
            @Param("status") TripStatus status);

    // Count trips for a user with a specific status
    long countByTransporterAndStatus(@Param("transporter") User transporter, @Param("status") TripStatus status);

    // Count trips for a user excluding certain statuses
    long countByTransporterAndStatusNotIn(@Param("transporter") User transporter,
            @Param("statuses") List<TripStatus> statuses);

    @org.springframework.data.jpa.repository.Query("SELECT t FROM Trip t WHERE LOWER(t.departureCity) LIKE LOWER(CONCAT('%', :departureCity, '%')) AND LOWER(t.arrivalCity) LIKE LOWER(CONCAT('%', :arrivalCity, '%')) AND t.departureDate >= :departureDate AND t.availableSpace >= :minSpace AND t.status = com.bagygo.bagygo_backend.enums.TripStatus.OPEN AND t.transporter.isVerified = true ORDER BY t.departureDate ASC")
    List<Trip> searchTrips(
            @org.springframework.data.repository.query.Param("departureCity") String departureCity,
            @org.springframework.data.repository.query.Param("arrivalCity") String arrivalCity,
            @org.springframework.data.repository.query.Param("departureDate") java.time.LocalDateTime departureDate,
            @org.springframework.data.repository.query.Param("minSpace") Double minSpace);

    @org.springframework.data.jpa.repository.Query("SELECT t FROM Trip t WHERE t.status = com.bagygo.bagygo_backend.enums.TripStatus.OPEN AND NOT EXISTS (SELECT br FROM BaggageRequest br WHERE br.trip = t AND br.isDedicatedTrip = true) ORDER BY t.departureDate ASC")
    List<Trip> findAvailableTripsForDashboard();
}