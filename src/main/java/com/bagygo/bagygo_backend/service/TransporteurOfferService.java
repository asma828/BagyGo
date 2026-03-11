package com.bagygo.bagygo_backend.service;

import com.bagygo.bagygo_backend.dto.request.CreateTransportOfferRequest;
import com.bagygo.bagygo_backend.dto.response.TransportOfferResponse;
import com.bagygo.bagygo_backend.entity.BaggageRequest;
import com.bagygo.bagygo_backend.entity.TransportOffer;
import com.bagygo.bagygo_backend.entity.Trip;
import com.bagygo.bagygo_backend.entity.User;
import com.bagygo.bagygo_backend.enums.OfferStatus;
import com.bagygo.bagygo_backend.enums.RequestStatus;
import com.bagygo.bagygo_backend.enums.TransportOfferStatus;
import com.bagygo.bagygo_backend.repository.BaggageRequestRepository;
import com.bagygo.bagygo_backend.repository.TransportOfferRepository;
import com.bagygo.bagygo_backend.repository.TripRepository;
import com.bagygo.bagygo_backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransporteurOfferService {

    private final TransportOfferRepository transportOfferRepository;
    private final UserRepository userRepository;
    private final BaggageRequestRepository baggageRequestRepository;
    private final TripRepository tripRepository;

    public TransporteurOfferService(
            TransportOfferRepository transportOfferRepository,
            UserRepository userRepository,
            BaggageRequestRepository baggageRequestRepository,
            TripRepository tripRepository
    ) {
        this.transportOfferRepository = transportOfferRepository;
        this.userRepository = userRepository;
        this.baggageRequestRepository = baggageRequestRepository;
        this.tripRepository = tripRepository;
    }

    public TransportOfferResponse createOffer(
            CreateTransportOfferRequest request,
            String email
    ) {

        User transporteur = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Trip trip = tripRepository.findById(request.getTripId())
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        BaggageRequest baggageRequest =
                baggageRequestRepository.findById(request.getBaggageRequestId())
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

        List<TransportOffer> offers =
                transportOfferRepository.findByBaggageRequestOrderByCreatedAtDesc(request);

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

    public List<TransportOfferResponse> getOffersForRequest(Long requestId) {

        BaggageRequest request = baggageRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

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

    public TransportOfferResponse counterOffer(Long id, Double price, User user) {

        TransportOffer offer = transportOfferRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offer not found"));

        if (!offer.getBaggageRequest().getSender().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        offer.setProposedPrice(price);
        offer.setStatus(TransportOfferStatus.COUNTERED);

        return TransportOfferResponse.from(
                transportOfferRepository.save(offer)
        );
    }


}
