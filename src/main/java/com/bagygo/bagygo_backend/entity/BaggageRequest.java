package com.bagygo.bagygo_backend.entity;

import com.bagygo.bagygo_backend.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "baggage_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaggageRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Column(nullable = false)
    private String departureCity;

    @Column(nullable = false)
    private String arrivalCity;

    @Column(nullable = false)
    private LocalDate desiredDate;

    @Column(nullable = false)
    private Double weightKg;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Double proposedPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RequestStatus status = RequestStatus.OPEN;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isFragile = false;

    private String imageUrl;

    @OneToMany(mappedBy = "baggageRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<TransportOffer> offers = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    private Trip trip;

    private String message;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isPaid = false;

    @Builder.Default
    private Boolean isDedicatedTrip = false;

    private Long acceptedOfferId;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}