package com.bagygo.bagygo_backend.service;

import com.bagygo.bagygo_backend.dto.request.CreateRatingRequest;
import com.bagygo.bagygo_backend.dto.response.RatingResponse;
import com.bagygo.bagygo_backend.dto.response.RatingSummaryResponse;
import com.bagygo.bagygo_backend.entity.BaggageRequest;
import com.bagygo.bagygo_backend.entity.Rating;
import com.bagygo.bagygo_backend.entity.User;
import com.bagygo.bagygo_backend.enums.NotificationType;
import com.bagygo.bagygo_backend.enums.RequestStatus;
import com.bagygo.bagygo_backend.dto.response.UserResponse;
import com.bagygo.bagygo_backend.repository.BaggageRequestRepository;
import com.bagygo.bagygo_backend.repository.RatingRepository;
import com.bagygo.bagygo_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RatingService {

    private final RatingRepository ratingRepository;
    private final BaggageRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public RatingResponse createRating(CreateRatingRequest req, User fromUser) {
        List<BaggageRequest> requests = requestRepository.findBySenderOrderByCreatedAtDesc(fromUser);
        BaggageRequest br = requests.stream()
                .filter(r -> r.getStatus() == RequestStatus.COMPLETED 
                        && r.getTrip() != null 
                        && r.getTrip().getTransporter().getId().equals(req.getToUserId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No completed deliveries found with this transporter."));

        User toUser = br.getTrip().getTransporter();

        Rating rating = Rating.builder()
                .fromUser(fromUser)
                .toUser(toUser)
                .request(br)
                .score(req.getScore())
                .comment(req.getComment())
                .createdAt(LocalDateTime.now())
                .build();

        Rating saved = ratingRepository.save(rating);

        // Update Transporter metrics
        Double newAverage = ratingRepository.calculateAverageRatingForUser(toUser.getId());
        toUser.setRating(newAverage != null ? newAverage : 0.0);
        toUser.setTotalDeliveries(toUser.getTotalDeliveries() + 1);
        userRepository.save(toUser);

        // Notify Transporter
        String msg = String.format("Vous avez reçu une note de %d étoiles de %s pour votre livraison récente.",
                req.getScore(), fromUser.getFirstName());
        notificationService.createNotification(toUser, msg, NotificationType.SYSTEM);

        return RatingResponse.from(saved);
    }

    public List<RatingResponse> getUserRatings(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return ratingRepository.findByToUserOrderByCreatedAtDesc(user).stream()
                .map(RatingResponse::from).toList();
    }

    public RatingSummaryResponse getSummary(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        List<Rating> ratings = ratingRepository.findByToUserOrderByCreatedAtDesc(user);
        
        Map<Integer, Long> distribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) distribution.put(i, 0L);
        
        for (Rating r : ratings) {
            distribution.put(r.getScore(), distribution.getOrDefault(r.getScore(), 0L) + 1);
        }
        
        List<RatingResponse> recent = ratings.stream()
                .limit(5)
                .map(RatingResponse::from)
                .collect(Collectors.toList());

        return RatingSummaryResponse.builder()
                .average(user.getRating())
                .total(ratings.size())
                .distribution(distribution)
                .recent(recent)
                .build();
    }

    public List<UserResponse> getRatableTransporters(User sender) {
        List<BaggageRequest> requests = requestRepository.findBySenderOrderByCreatedAtDesc(sender);
        
        return requests.stream()
                .filter(br -> br.getStatus() == RequestStatus.COMPLETED && br.getTrip() != null)
                .map(br -> br.getTrip().getTransporter())
                .distinct()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }
}