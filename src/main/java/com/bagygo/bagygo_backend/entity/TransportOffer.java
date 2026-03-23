package com.bagygo.bagygo_backend.entity;

import com.bagygo.bagygo_backend.enums.TransportOfferStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "transport_offers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransportOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double proposedPrice;

    @Enumerated(EnumType.STRING)
    private TransportOfferStatus status;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "baggage_request_id", nullable = false)
    private BaggageRequest baggageRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    private Trip trip;
}
