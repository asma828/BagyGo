package com.bagygo.bagygo_backend.dto.response;

import com.bagygo.bagygo_backend.entity.Rating;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RatingResponse {
    private Long id;
    private UserResponse fromUser;
    private UserResponse toUser;
    private Long requestId;
    private int score;
    private String comment;
    private LocalDateTime createdAt;

    public static RatingResponse from(Rating r) {
        RatingResponse rr = new RatingResponse();
        rr.setId(r.getId());
        rr.setFromUser(UserResponse.from(r.getFromUser()));
        rr.setToUser(UserResponse.from(r.getToUser()));
        rr.setRequestId(r.getRequest().getId());
        rr.setScore(r.getScore());
        rr.setComment(r.getComment());
        rr.setCreatedAt(r.getCreatedAt());
        return rr;
    }
}