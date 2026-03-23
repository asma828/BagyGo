package com.bagygo.bagygo_backend.service;

import com.bagygo.bagygo_backend.dto.request.CreateBaggageRequestRequest;
import com.bagygo.bagygo_backend.dto.response.BaggageRequestResponse;
import com.bagygo.bagygo_backend.entity.BaggageRequest;
import com.bagygo.bagygo_backend.entity.User;
import com.bagygo.bagygo_backend.enums.RequestStatus;
import com.bagygo.bagygo_backend.entity.Trip;
import com.bagygo.bagygo_backend.repository.BaggageRequestRepository;
import com.bagygo.bagygo_backend.repository.TripRepository;
import com.bagygo.bagygo_backend.dto.request.RespondToRequestRequest;
import com.bagygo.bagygo_backend.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BaggageRequestService {

    private final BaggageRequestRepository repository;
    private final TripRepository tripRepository;
    private final NotificationService notificationService;

    public BaggageRequestResponse create(CreateBaggageRequestRequest req, User sender) {
        BaggageRequest br = BaggageRequest.builder()
                .sender(sender)
                .departureCity(req.getDepartureCity())
                .arrivalCity(req.getArrivalCity())
                .desiredDate(req.getDesiredDate())
                .weightKg(req.getWeightKg())
                .description(req.getDescription())
                .proposedPrice(req.getProposedPrice())
                .isFragile(req.getIsFragile() != null && req.getIsFragile())
                .message(req.getMessage())
                .isDedicatedTrip(false)
                .build();

        if (req.getTripId() != null) {
            Trip trip = tripRepository.findById(req.getTripId())
                    .orElseThrow(() -> new IllegalArgumentException("Trip not found: " + req.getTripId()));

            if (trip.getTransporter() == null) {
                System.err.println("CRITICAL: Trip " + trip.getId() + " has no transporter!");
                throw new IllegalStateException("Cannot send request to a trip without a transporter.");
            }

            // 1. Validation: Available Space
            if (req.getWeightKg() > trip.getAvailableSpace()) {
                throw new IllegalArgumentException("Requested weight exceeds trip's available space ("
                        + trip.getAvailableSpace() + " kg).");
            }

            // 2. Validation: Trip Date
            if (trip.getDepartureDate().isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("Trip expired");
            }

            br.setTrip(trip);
            br.setStatus(RequestStatus.PENDING);

            BaggageRequest saved = repository.save(br);
            System.out.println("Saved BaggageRequest " + saved.getId() + " for trip " + trip.getId());

            // Notify transporter
            String msg = String.format("Nouvelle demande reçue de %s pour votre trajet %s -> %s",
                    sender.getFirstName(),
                    req.getDepartureCity(),
                    req.getArrivalCity());
            notificationService.createNotification(trip.getTransporter(), msg, NotificationType.REQUEST_RECEIVED);

            return BaggageRequestResponse.from(saved);
        }

        return BaggageRequestResponse.from(repository.save(br));
    }

    public List<BaggageRequestResponse> getMySenderRequests(User sender) {
        return repository.findBySenderOrderByCreatedAtDesc(sender)
                .stream().map(BaggageRequestResponse::from).toList();
    }

    public List<BaggageRequestResponse> getOpenRequests() {
        return repository.findByStatusOrderByCreatedAtDesc(RequestStatus.OPEN)
                .stream().map(BaggageRequestResponse::from).toList();
    }

    public List<BaggageRequestResponse> getRequestsForTransporter(User transporter) {
        return repository.findByTrip_TransporterOrderByCreatedAtDesc(transporter)
                .stream().map(BaggageRequestResponse::from).toList();
    }

    public BaggageRequestResponse getById(Long id) {
        return BaggageRequestResponse.from(findOrThrow(id));
    }

    public BaggageRequestResponse cancel(Long id, User sender) {
        BaggageRequest br = findOrThrow(id);
        if (!br.getSender().getId().equals(sender.getId())) {
            throw new IllegalArgumentException("Not your request");
        }
        br.setStatus(RequestStatus.CANCELLED);
        return BaggageRequestResponse.from(repository.save(br));
    }

    public BaggageRequestResponse updateStatus(Long id, RequestStatus newStatus, User user) {
        BaggageRequest br = findOrThrow(id);

        boolean isTransporter = br.getTrip() != null && br.getTrip().getTransporter() != null
                && br.getTrip().getTransporter().getId().equals(user.getId());
        boolean isSender = br.getSender().getId().equals(user.getId());

        if (!isTransporter && !isSender) {
            throw new IllegalArgumentException("You are not authorized to update this request.");
        }

        // Validate state transitions & authorization
        if (newStatus == RequestStatus.IN_TRANSIT) {
            if (!isTransporter)
                throw new IllegalArgumentException("Only the transporter can start the delivery.");
            if (br.getStatus() != RequestStatus.ACCEPTED)
                throw new IllegalStateException("Request must be ACCEPTED first.");
        } else if (newStatus == RequestStatus.DELIVERED) {
            if (!isTransporter)
                throw new IllegalArgumentException("Only the transporter can mark as delivered.");
            if (br.getStatus() != RequestStatus.IN_TRANSIT)
                throw new IllegalStateException("Request must be IN_TRANSIT first.");
        } else if (newStatus == RequestStatus.COMPLETED) {
            if (!isSender)
                throw new IllegalArgumentException("Only the sender can confirm reception.");
            if (br.getStatus() != RequestStatus.DELIVERED)
                throw new IllegalStateException("Request must be DELIVERED first.");
        } else {
            throw new IllegalArgumentException("Invalid status update.");
        }

        br.setStatus(newStatus);
        BaggageRequest saved = repository.save(br);

        // Notify appropriate party
        if (newStatus == RequestStatus.IN_TRANSIT) {
            notificationService.createNotification(br.getSender(),
                    "Votre colis est maintenant en cours de livraison (IN TRANSIT).", NotificationType.SYSTEM);
        } else if (newStatus == RequestStatus.DELIVERED) {
            notificationService.createNotification(br.getSender(),
                    "Votre colis a été livré par le transporteur (DELIVERED). Veuillez confirmer la réception.",
                    NotificationType.SYSTEM);
        } else if (newStatus == RequestStatus.COMPLETED) {
            if (br.getTrip() != null && br.getTrip().getTransporter() != null) {
                notificationService.createNotification(br.getTrip().getTransporter(),
                        "L'expéditeur a confirmé la réception du colis. (COMPLETED).", NotificationType.SYSTEM);
            }
        }

        return BaggageRequestResponse.from(saved);
    }

    public BaggageRequestResponse respondToRequest(Long requestId, RespondToRequestRequest req, User transporter) {
        BaggageRequest br = findOrThrow(requestId);
        if (br.getStatus() != RequestStatus.OPEN) {
            throw new IllegalStateException("Request is not OPEN for response.");
        }

        // Create dedicated Trip
        Trip trip = Trip.builder()
                .transporter(transporter)
                .departureCity(br.getDepartureCity())
                .arrivalCity(br.getArrivalCity())
                .departureDate(req.getDepartureDate())
                .estimatedArrival(req.getEstimatedArrival())
                .availableSpace(br.getWeightKg())
                .pricePerKg(req.getPricePerKg())
                .notes(req.getNotes())
                .status(com.bagygo.bagygo_backend.enums.TripStatus.OPEN)
                .build();

        Trip savedTrip = tripRepository.save(trip);
        br.setTrip(savedTrip);
        br.setStatus(RequestStatus.PENDING);
        br.setIsDedicatedTrip(true);

        BaggageRequest saved = repository.save(br);
        
        // Notify sender
        notificationService.createNotification(br.getSender(), 
            "Un transporteur a répondu à votre demande ! Vérifiez votre tableau de bord.", 
            NotificationType.SYSTEM);

        return BaggageRequestResponse.from(saved);
    }

    public BaggageRequestResponse acceptRequest(Long id, User user) {
        BaggageRequest br = findOrThrow(id);
        
        if (Boolean.TRUE.equals(br.getIsDedicatedTrip())) {
            // Flow B: Sender accepts response
            if (!br.getSender().getId().equals(user.getId())) {
                throw new IllegalArgumentException("Only the sender can accept a transporter's response.");
            }
        } else {
            // Flow A: Transporter accepts request
            if (br.getTrip() == null || !br.getTrip().getTransporter().getId().equals(user.getId())) {
                throw new IllegalArgumentException("Only the transporter can accept a sender's request.");
            }
        }

        if (br.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Request must be in PENDING status to be accepted.");
        }

        br.setStatus(RequestStatus.ACCEPTED);

        // If Flow A (Request sent to existing trip), update trip available space
        if (!Boolean.TRUE.equals(br.getIsDedicatedTrip()) && br.getTrip() != null) {
            Trip trip = br.getTrip();
            double newSpace = trip.getAvailableSpace() - br.getWeightKg();
            if (newSpace < 0) {
                throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, 
                    "Not enough space on this trip anymore.");
            }
            trip.setAvailableSpace(newSpace);
            tripRepository.save(trip);
        }

        BaggageRequest saved = repository.save(br);

        // Notify appropriate party
        if (Boolean.TRUE.equals(br.getIsDedicatedTrip())) {
            notificationService.createNotification(br.getTrip().getTransporter(), 
                "Votre offre a été acceptée par l'expéditeur !", 
                NotificationType.SYSTEM);
        } else {
            notificationService.createNotification(br.getSender(), 
                "Votre demande d'expédition a été acceptée par le transporteur !", 
                NotificationType.SYSTEM);
        }

        return BaggageRequestResponse.from(saved);
    }

    public BaggageRequestResponse rejectRequest(Long id, User user) {
        BaggageRequest br = findOrThrow(id);

        if (Boolean.TRUE.equals(br.getIsDedicatedTrip())) {
            // Flow B: Sender rejects response
            if (!br.getSender().getId().equals(user.getId())) {
                throw new IllegalArgumentException("Only the sender can reject a transporter's response.");
            }
        } else {
            // Flow A: Transporter rejects request
            if (br.getTrip() == null || !br.getTrip().getTransporter().getId().equals(user.getId())) {
                throw new IllegalArgumentException("Only the transporter can reject a sender's request.");
            }
        }
        
        br.setStatus(RequestStatus.REJECTED);
        
        // If it was a dedicated trip created specifically for this request, cancel the trip too
        if (Boolean.TRUE.equals(br.getIsDedicatedTrip()) && br.getTrip() != null) {
            Trip trip = br.getTrip();
            trip.setStatus(com.bagygo.bagygo_backend.enums.TripStatus.CANCELLED);
            tripRepository.save(trip);
        }

        BaggageRequest saved = repository.save(br);

        // Notify appropriate party
        if (Boolean.TRUE.equals(br.getIsDedicatedTrip())) {
            notificationService.createNotification(br.getTrip().getTransporter(), 
                "Votre offre a été refusée par l'expéditeur.", 
                NotificationType.SYSTEM);
        } else {
            notificationService.createNotification(br.getSender(), 
                "Votre demande d'expédition a été refusée par le transporteur.", 
                NotificationType.SYSTEM);
        }

        return BaggageRequestResponse.from(saved);
    }

    public BaggageRequest findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("BaggageRequest not found: " + id));
    }
}