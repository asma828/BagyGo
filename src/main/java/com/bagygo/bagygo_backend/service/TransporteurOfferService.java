package com.bagygo.bagygo_backend.service;

import com.bagygo.bagygo_backend.dto.request.CreateTransportOfferRequest;
import com.bagygo.bagygo_backend.dto.response.TransportOfferResponse;
import com.bagygo.bagygo_backend.entity.BaggageRequest;
import com.bagygo.bagygo_backend.entity.TransportOffer;
import com.bagygo.bagygo_backend.entity.Trip;
import com.bagygo.bagygo_backend.entity.User;
import com.bagygo.bagygo_backend.enums.RequestStatus;
import com.bagygo.bagygo_backend.enums.TransportOfferStatus;
import com.bagygo.bagygo_backend.repository.TransportOfferRepository;
import com.bagygo.bagygo_backend.repository.TripRepository;
import com.bagygo.bagygo_backend.repository.UserRepository;
import com.bagygo.bagygo_backend.repository.BaggageRequestRepository;
import com.bagygo.bagygo_backend.enums.NotificationType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransporteurOfferService {

        private final TransportOfferRepository transportOfferRepository;
        private final UserRepository userRepository;
        private final BaggageRequestRepository baggageRequestRepository;
        private final TripRepository tripRepository;
        private final NotificationService notificationService;

        public TransporteurOfferService(
                        TransportOfferRepository transportOfferRepository,
                        UserRepository userRepository,
                        BaggageRequestRepository baggageRequestRepository,
                        TripRepository tripRepository,
                        NotificationService notificationService) {
                this.transportOfferRepository = transportOfferRepository;
                this.userRepository = userRepository;
                this.baggageRequestRepository = baggageRequestRepository;
                this.tripRepository = tripRepository;
                this.notificationService = notificationService;
        }

        public TransportOfferResponse createOffer(
                        CreateTransportOfferRequest request,
                        String email) {

                User transporteur = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                Trip trip = tripRepository.findById(request.getTripId())
                                .orElseThrow(() -> new RuntimeException("Trip not found"));

                BaggageRequest baggageRequest = baggageRequestRepository.findById(request.getBaggageRequestId())
                                .orElseThrow(() -> new RuntimeException("Baggage request not found"));

                TransportOffer offer = new TransportOffer();
                offer.setTrip(trip);
                offer.setBaggageRequest(baggageRequest);
                offer.setProposedPrice(request.getProposedPrice());
                offer.setStatus(TransportOfferStatus.PENDING);
                offer.setUser(transporteur);

                TransportOffer saved = transportOfferRepository.save(offer);

                return TransportOfferResponse.from(saved);
        }

        public void acceptOffer(Long offerId, String email) {

                User expeditor = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                TransportOffer offer = transportOfferRepository.findById(offerId)
                                .orElseThrow(() -> new RuntimeException("Offer not found"));

                BaggageRequest request = offer.getBaggageRequest();

                if (!request.getSender().getId().equals(expeditor.getId())) {
                        throw new RuntimeException("Access denied");
                }

                List<TransportOffer> offers = transportOfferRepository
                                .findByBaggageRequestOrderByCreatedAtDesc(request);

                for (TransportOffer o : offers) {
                        if (o.getId().equals(offerId)) {
                                o.setStatus(TransportOfferStatus.ACCEPTED);
                        } else {
                                o.setStatus(TransportOfferStatus.REJECTED);
                        }
                }

                request.setStatus(RequestStatus.ACCEPTED);

                transportOfferRepository.saveAll(offers);
                baggageRequestRepository.save(request);

                // Notify the transporter
                String msg = String.format("Votre offre pour le trajet %s -> %s a été acceptée par %s",
                                request.getDepartureCity(),
                                request.getArrivalCity(),
                                expeditor.getFirstName());
                notificationService.createNotification(offer.getUser(), msg, NotificationType.OFFER_ACCEPTED);
        }

        public void rejectOffer(Long offerId, String email) {

                User expeditor = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                TransportOffer offer = transportOfferRepository.findById(offerId)
                                .orElseThrow(() -> new RuntimeException("Offer not found"));

                if (!offer.getBaggageRequest().getSender().getId()
                                .equals(expeditor.getId())) {
                        throw new RuntimeException("Access denied");
                }

                offer.setStatus(TransportOfferStatus.REJECTED);
                transportOfferRepository.save(offer);
        }

        /**
         * Called by transporter to accept an incoming request (offer) for their trip.
         * Uses offer.getUser() for access control to avoid NPE when trip is null.
         */
        public void transporterAcceptOffer(Long offerId, String email) {

                User transporter = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                TransportOffer offer = transportOfferRepository.findById(offerId)
                                .orElseThrow(() -> new RuntimeException("Offer not found"));

                // Access check: the offer must belong to this transporter
                if (!offer.getUser().getId().equals(transporter.getId())) {
                        throw new RuntimeException("Access denied: you are not the transporter for this offer");
                }

                offer.setStatus(TransportOfferStatus.ACCEPTED);
                BaggageRequest request = offer.getBaggageRequest();
                request.setStatus(RequestStatus.ACCEPTED);
                request.setAcceptedOfferId(offerId);

                transportOfferRepository.save(offer);
                baggageRequestRepository.save(request);

                // Notify the sender
                String msg = String.format("Votre demande pour le trajet %s -> %s a été acceptée par %s",
                                request.getDepartureCity(),
                                request.getArrivalCity(),
                                transporter.getFirstName());
                notificationService.createNotification(request.getSender(), msg, NotificationType.OFFER_ACCEPTED);
        }

        /**
         * Called by transporter to reject/decline an incoming request for their trip.
         */
        public void transporterRejectOffer(Long offerId, String email) {

                User transporter = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                TransportOffer offer = transportOfferRepository.findById(offerId)
                                .orElseThrow(() -> new RuntimeException("Offer not found"));

                // Access check: the offer must belong to this transporter
                if (!offer.getUser().getId().equals(transporter.getId())) {
                        throw new RuntimeException("Access denied: you are not the transporter for this offer");
                }

                offer.setStatus(TransportOfferStatus.REJECTED);
                transportOfferRepository.save(offer);
        }

        public List<TransportOfferResponse> getOffersForRequest(Long requestId) {

                BaggageRequest request = baggageRequestRepository.findById(requestId)
                                .orElseThrow(() -> new RuntimeException("Request not found"));

                // Security: This can be expanded to check if the user is the sender or common
                // logic

                return transportOfferRepository
                                .findByBaggageRequestOrderByCreatedAtDesc(request)
                                .stream()
                                .map(TransportOfferResponse::from)
                                .toList();
        }

        public List<TransportOfferResponse> getMyOffers(User user) {

                return transportOfferRepository
                                .findByUserOrderByCreatedAtDesc(user)
                                .stream()
                                .map(TransportOfferResponse::from)
                                .toList();
        }

        public List<TransportOfferResponse> getMyOffersByEmail(String email) {
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));
                return getMyOffers(user);
        }

        public TransportOfferResponse counterOffer(Long id, Double price, User user) {

                TransportOffer offer = transportOfferRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Offer not found"));

                if (!offer.getBaggageRequest().getSender().getId().equals(user.getId())) {
                        throw new RuntimeException("Access denied");
                }

                offer.setProposedPrice(price);
                offer.setStatus(TransportOfferStatus.COUNTERED);

                return TransportOfferResponse.from(
                                transportOfferRepository.save(offer));
        }

        public TransportOfferResponse counterOfferByEmail(Long id, Double price, String email) {
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));
                return counterOffer(id, price, user);
        }

        public void updateStatus(Long requestId, RequestStatus newStatus, String email) {
                BaggageRequest request = baggageRequestRepository.findById(requestId)
                                .orElseThrow(() -> new RuntimeException("Request not found"));

                // Only transporter of the linked trip can update status
                if (request.getTrip() == null || !request.getTrip().getTransporter().getEmail().equals(email)) {
                        throw new RuntimeException("Access denied: Not the transporter of this trip");
                }

                if (!request.getIsPaid()) {
                        throw new RuntimeException("Cannot update status: Request is not paid yet");
                }

                request.setStatus(newStatus);
                baggageRequestRepository.save(request);
        }

}
