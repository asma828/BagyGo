package com.bagygo.bagygo_backend.service;

import com.bagygo.bagygo_backend.dto.request.CreateBaggageRequestRequest;
import com.bagygo.bagygo_backend.dto.response.BaggageRequestDetailsResponse;
import com.bagygo.bagygo_backend.dto.response.BaggageRequestResponse;
import com.bagygo.bagygo_backend.dto.response.TransportOfferResponse;
import com.bagygo.bagygo_backend.entity.BaggageRequest;
import com.bagygo.bagygo_backend.entity.TransportOffer;
import com.bagygo.bagygo_backend.entity.User;
import com.bagygo.bagygo_backend.repository.BaggageRequestRepository;
import com.bagygo.bagygo_backend.repository.TransportOfferRepository;
import com.bagygo.bagygo_backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BaggageRequestService {

    private final BaggageRequestRepository baggageRequestRepository;
    private final TransportOfferRepository transportOfferRepository;
    private final UserRepository userRepository;

    public BaggageRequestService(
            BaggageRequestRepository baggageRequestRepository,
            TransportOfferRepository transportOfferRepository,
            UserRepository userRepository
    ) {
        this.baggageRequestRepository = baggageRequestRepository;
        this.transportOfferRepository = transportOfferRepository;
        this.userRepository = userRepository;
    }

    public BaggageRequestDetailsResponse getDetails(Long requestId, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BaggageRequest request = baggageRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("BaggageRequest not found"));

        if (!request.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        List<TransportOffer> offers =
                transportOfferRepository.findByBaggageRequest(request);

        List<TransportOfferResponse> offerResponses = offers.stream()
                .map(offer -> new TransportOfferResponse(
                        offer.getId(),
                        offer.getProposedPrice(),
                        offer.getStatus().name(),
                        offer.getUser().getFirstName()
                ))
                .collect(Collectors.toList());

        return new BaggageRequestDetailsResponse(
                request.getId(),
                request.getDescription(),
                request.getWeight(),
                request.getProposedPrice(),
                request.getStatus().name(),
                request.getCreatedAt(),
                offerResponses
        );
    }

    public BaggageRequestResponse create(CreateBaggageRequestRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BaggageRequest baggageRequest = new BaggageRequest();
        baggageRequest.setDescription(request.getDescription());
        baggageRequest.setWeight(request.getWeight());
        baggageRequest.setProposedPrice(request.getProposedPrice());
        baggageRequest.setUser(user);

        BaggageRequest saved = baggageRequestRepository.save(baggageRequest);

        return new BaggageRequestResponse(
                saved.getId(),
                saved.getDescription(),
                saved.getWeight(),
                saved.getProposedPrice(),
                saved.getStatus().name(),
                saved.getCreatedAt()
        );
    }

    public List<BaggageRequestResponse> getMyRequests(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return baggageRequestRepository.findByUser(user)
                .stream()
                .map(request -> new BaggageRequestResponse(
                        request.getId(),
                        request.getDescription(),
                        request.getWeight(),
                        request.getProposedPrice(),
                        request.getStatus().name(),
                        request.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }


}
