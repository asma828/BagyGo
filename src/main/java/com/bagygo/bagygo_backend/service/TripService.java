package com.bagygo.bagygo_backend.service;

import com.bagygo.bagygo_backend.dto.request.CreateTripRequest;
import com.bagygo.bagygo_backend.dto.response.TripResponse;
import com.bagygo.bagygo_backend.entity.Trip;
import com.bagygo.bagygo_backend.entity.User;
import com.bagygo.bagygo_backend.repository.BaggageRequestRepository;
import com.bagygo.bagygo_backend.repository.TripRepository;
import com.bagygo.bagygo_backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TripService {

    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final BaggageRequestRepository baggageRequestRepository;

    public TripService(
            TripRepository tripRepository,
            UserRepository userRepository,
            BaggageRequestRepository baggageRequestRepository) {
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
        this.baggageRequestRepository = baggageRequestRepository;
    }

    public TripResponse create(CreateTripRequest request, String email) {

        User transporteur = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Trip trip = Trip.builder()
                .transporter(transporteur)
                .departureCity(request.getDepartureCity())
                .arrivalCity(request.getArrivalCity())
                .departureDate(request.getDepartureDate())
                .estimatedArrival(request.getEstimatedArrival())
                .availableSpace(request.getAvailableSpace())
                .pricePerKg(request.getPricePerKg())
                .notes(request.getNotes())
                .build();

        Trip saved = tripRepository.save(trip);

        return mapToResponse(saved);
    }

    public List<TripResponse> getMyTrips(String email) {

        User transporteur = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return tripRepository.findByTransporterOrderByDepartureDateDesc(transporteur)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<TripResponse> searchTrips(String departureCity, String arrivalCity, LocalDateTime date, Double weight) {
        return tripRepository.searchTrips(departureCity, arrivalCity, date, weight)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public TripResponse updateLocation(Long tripId, Double lat, Double lng, String email) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        if (!trip.getTransporter().getEmail().equals(email)) {
            throw new RuntimeException("Access denied");
        }

        trip.setCurrentLat(lat);
        trip.setCurrentLng(lng);
        return mapToResponse(tripRepository.save(trip));
    }

    public TripResponse getById(Long id) {
        return mapToResponse(tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found")));
    }

    public TripResponse cancel(Long id, String email) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        if (!trip.getTransporter().getEmail().equals(email)) {
            throw new RuntimeException("Access denied");
        }

        // Only allow cancellation if OPEN or FULL
        if (trip.getStatus() != com.bagygo.bagygo_backend.enums.TripStatus.OPEN && 
            trip.getStatus() != com.bagygo.bagygo_backend.enums.TripStatus.FULL) {
            throw new RuntimeException("Cannot cancel trip in current status: " + trip.getStatus());
        }

        trip.setStatus(com.bagygo.bagygo_backend.enums.TripStatus.CANCELLED);
        return mapToResponse(tripRepository.save(trip));
    }

    public TripResponse updateStatus(Long id, com.bagygo.bagygo_backend.enums.TripStatus status, String email) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        if (!trip.getTransporter().getEmail().equals(email)) {
            throw new RuntimeException("Access denied");
        }

        trip.setStatus(status);
        Trip savedTrip = tripRepository.save(trip);

        // Synchronize BaggageRequest status
        List<com.bagygo.bagygo_backend.entity.BaggageRequest> requests = baggageRequestRepository.findByTrip(savedTrip);
        for (com.bagygo.bagygo_backend.entity.BaggageRequest req : requests) {
            if (status == com.bagygo.bagygo_backend.enums.TripStatus.IN_PROGRESS) {
                req.setStatus(com.bagygo.bagygo_backend.enums.RequestStatus.IN_TRANSIT);
            } else if (status == com.bagygo.bagygo_backend.enums.TripStatus.COMPLETED) {
                req.setStatus(com.bagygo.bagygo_backend.enums.RequestStatus.DELIVERED);
            }
            baggageRequestRepository.save(req);
        }

        return mapToResponse(savedTrip);
    }

    private TripResponse mapToResponse(Trip trip) {
        return TripResponse.from(trip);
    }
}
