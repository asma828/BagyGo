package com.bagygo.bagygo_backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateRatingRequest {

    @NotNull
    private Long toUserId;

    @NotNull @Min(1) @Max(5)
    private Integer score;

    @Size(max = 400)
    private String comment;
}