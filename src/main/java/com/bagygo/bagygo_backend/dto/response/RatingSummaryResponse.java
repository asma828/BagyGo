package com.bagygo.bagygo_backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class RatingSummaryResponse {
    private double average;           // rounded to 1 decimal
    private int    total;
    private Map<Integer, Long> distribution; // 5→count, 4→count … 1→count
    private List<RatingResponse> recent;     // latest 5
}