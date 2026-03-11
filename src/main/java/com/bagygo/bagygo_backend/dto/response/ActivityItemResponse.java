package com.bagygo.bagygo_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityItemResponse {
    private Long id;
    private String type;         // e.g., NEW_OFFER, DELIVERED, RATING, ACCEPTED
    private String title;
    private String description;
    private LocalDateTime timestamp;
}