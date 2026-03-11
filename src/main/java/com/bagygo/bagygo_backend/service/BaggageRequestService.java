package com.bagygo.bagygo_backend.service;

import com.bagygo.bagygo_backend.dto.request.CreateBaggageRequestRequest;
import com.bagygo.bagygo_backend.dto.response.BaggageRequestResponse;
import com.bagygo.bagygo_backend.entity.BaggageRequest;
import com.bagygo.bagygo_backend.entity.User;
import com.bagygo.bagygo_backend.enums.RequestStatus;
import com.bagygo.bagygo_backend.repository.BaggageRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BaggageRequestService {

    private final BaggageRequestRepository repository;

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
                .build();

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

    public BaggageRequest findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("BaggageRequest not found: " + id));
    }
}