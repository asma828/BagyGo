package com.bagygo.bagygo_backend.service;

import com.bagygo.bagygo_backend.dto.request.CreateRatingRequest;
import com.bagygo.bagygo_backend.dto.response.RatingResponse;
import com.bagygo.bagygo_backend.dto.response.RatingSummaryResponse;
import com.bagygo.bagygo_backend.entity.Rating;
import com.bagygo.bagygo_backend.entity.User;
import com.bagygo.bagygo_backend.repository.RatingRepository;
import com.bagygo.bagygo_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final UserRepository   userRepository;

    // ── Submit a rating ───────────────────────────────────

    @Transactional
    public RatingResponse create(CreateRatingRequest req, User from) {

        User toUser = userRepository.findById(req.getToUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + req.getToUserId()));

        if (from.getId().equals(toUser.getId())) {
            throw new IllegalArgumentException("You cannot rate yourself.");
        }

        Rating rating = Rating.builder()
                .fromUser(from)
                .toUser(toUser)
                .score(req.getScore())
                .comment(req.getComment())
                .build();

        ratingRepository.save(rating);

        // Recalculate and persist the recipient's average
        Double avg = ratingRepository.findAverageRatingForUser(toUser);
        toUser.setRating(avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0);
        userRepository.save(toUser);

        return RatingResponse.from(rating);
    }

    // ── Ratings received by user ──────────────────────────

    public List<RatingResponse> getReceivedByUser(Long userId) {
        User user = findUserOrThrow(userId);
        return ratingRepository.findByToUserOrderByCreatedAtDesc(user)
                .stream().map(RatingResponse::from).toList();
    }

    // ── Summary for the current user ─────────────────────

    public RatingSummaryResponse getMySummary(User user) {
        return buildSummary(user);
    }

    // ── Summary for any public profile ───────────────────

    public RatingSummaryResponse getSummaryForUser(Long userId) {
        return buildSummary(findUserOrThrow(userId));
    }

    // ── Helpers ───────────────────────────────────────────

    private RatingSummaryResponse buildSummary(User user) {
        List<Rating> all = ratingRepository.findByToUserOrderByCreatedAtDesc(user);

        double avg = all.stream().mapToInt(Rating::getScore).average().orElse(0.0);
        avg = Math.round(avg * 10.0) / 10.0;

        // Descending star distribution 5 → 1
        Map<Integer, Long> dist = new LinkedHashMap<>();
        for (int star = 5; star >= 1; star--) {
            final int s = star;
            dist.put(s, all.stream().filter(r -> r.getScore() == s).count());
        }

        List<RatingResponse> recent = all.stream()
                .limit(5)
                .map(RatingResponse::from)
                .toList();

        return RatingSummaryResponse.builder()
                .average(avg)
                .total(all.size())
                .distribution(dist)
                .recent(recent)
                .build();
    }

    private User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }
}