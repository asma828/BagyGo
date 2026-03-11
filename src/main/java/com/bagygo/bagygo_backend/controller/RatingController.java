package com.bagygo.bagygo_backend.controller;

import com.bagygo.bagygo_backend.dto.request.CreateRatingRequest;
import com.bagygo.bagygo_backend.dto.response.RatingResponse;
import com.bagygo.bagygo_backend.dto.response.RatingSummaryResponse;
import com.bagygo.bagygo_backend.entity.User;
import com.bagygo.bagygo_backend.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    /** Submit a new rating */
    @PostMapping
    public ResponseEntity<RatingResponse> submit(
            @Valid @RequestBody CreateRatingRequest req,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ratingService.create(req, user));
    }

    /** My ratings summary (avg + distribution + recent 5) */
    @GetMapping("/me/summary")
    public ResponseEntity<RatingSummaryResponse> mySummary(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ratingService.getMySummary(user));
    }

    /** Any user's public rating summary */
    @GetMapping("/user/{userId}/summary")
    public ResponseEntity<RatingSummaryResponse> userSummary(@PathVariable Long userId) {
        return ResponseEntity.ok(ratingService.getSummaryForUser(userId));
    }

    /** Full list of ratings I have received */
    @GetMapping("/me")
    public ResponseEntity<?> myRatings(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ratingService.getReceivedByUser(user.getId()));
    }
}