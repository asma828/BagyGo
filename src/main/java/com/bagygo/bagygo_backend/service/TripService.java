package com.bagygo.bagygo_backend.service;

import com.bagygo.bagygo_backend.dto.request.CreateTripRequest;
import com.bagygo.bagygo_backend.dto.response.TripResponse;
import com.bagygo.bagygo_backend.entity.Trip;
import com.bagygo.bagygo_backend.entity.User;
import com.bagygo.bagygo_backend.repository.TripRepository;
import com.bagygo.bagygo_backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TripService {

    private final TripRepository tripRepository;
    private final UserRepository userRepository;

    public TripService(
            TripRepository tripRepository,
            UserRepository userRepository
    ) {
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
    }

    public TripResponse create(CreateTripRequest request, String email) {

        User transporteur = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Trip trip = new Trip();
        trip.setDepartureCity(request.getDepartureCity());
        trip.setArrivalCity(request.getArrivalCity());
        trip.setDepartureDate(request.getDepartureDate());
        trip.setAvailableSpace(request.getAvailableSpace());
//        trip.setUser(transporteur);

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

    private TripResponse mapToResponse(Trip trip) {
        return TripResponse.from(trip);
    }
}
