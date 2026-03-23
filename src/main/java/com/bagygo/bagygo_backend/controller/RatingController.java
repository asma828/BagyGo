package com.bagygo.bagygo_backend.controller;

import com.bagygo.bagygo_backend.dto.request.CreateRatingRequest;
import com.bagygo.bagygo_backend.dto.response.RatingResponse;
import com.bagygo.bagygo_backend.dto.response.RatingSummaryResponse;
import com.bagygo.bagygo_backend.dto.response.UserResponse;
import com.bagygo.bagygo_backend.entity.User;
import com.bagygo.bagygo_backend.service.RatingService;
import com.bagygo.bagygo_backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<RatingResponse> create(
            @Valid @RequestBody CreateRatingRequest req,
            Authentication auth) {
        User user = userService.getCurrentUser(auth.getName());
        return ResponseEntity.ok(ratingService.createRating(req, user));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<RatingResponse>> getUserRatings(@PathVariable("id") Long id) {
        return ResponseEntity.ok(ratingService.getUserRatings(id));
    }

    @GetMapping("/my-ratings")
    public ResponseEntity<List<RatingResponse>> getMyRatings(Authentication auth) {
        User user = userService.getCurrentUser(auth.getName());
        return ResponseEntity.ok(ratingService.getUserRatings(user.getId()));
    }

    @GetMapping("/my-summary")
    public ResponseEntity<RatingSummaryResponse> getMySummary(Authentication auth) {
        User user = userService.getCurrentUser(auth.getName());
        return ResponseEntity.ok(ratingService.getSummary(user.getId()));
    }

    @GetMapping("/ratable-transporters")
    public ResponseEntity<List<UserResponse>> getRatableTransporters(Authentication auth) {
        User user = userService.getCurrentUser(auth.getName());
        return ResponseEntity.ok(ratingService.getRatableTransporters(user));
    }
}