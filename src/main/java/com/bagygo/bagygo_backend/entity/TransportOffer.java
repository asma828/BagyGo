package com.bagygo.bagygo_backend.entity;

import com.bagygo.bagygo_backend.enums.TransportOfferStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransportOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double proposedPrice;

    @Enumerated(EnumType.STRING)
    private TransportOfferStatus status;

    private LocalDateTime createdAt;

    @ManyToOne
    private User user;

    @ManyToOne
    private BaggageRequest baggageRequest;

    @OneToOne
    private Trip trip;
}

