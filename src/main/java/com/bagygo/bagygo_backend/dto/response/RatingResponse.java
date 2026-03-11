package com.bagygo.bagygo_backend.dto.response;

import com.bagygo.bagygo_backend.entity.Rating;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RatingResponse {

    private Long id;
    private UserResponse fromUser;
    private UserResponse toUser;
    private Integer score;
    private String comment;
    private LocalDateTime createdAt;

    public static RatingResponse from(Rating r) {
        RatingResponse res = new RatingResponse();
        res.setId(r.getId());
//        res.setFromUser(UserResponse.from(r.getFromUser()));
//        res.setToUser(UserResponse.from(r.getToUser()));
        res.setScore(r.getScore());
        res.setComment(r.getComment());
        res.setCreatedAt(r.getCreatedAt());
        return res;
    }
}