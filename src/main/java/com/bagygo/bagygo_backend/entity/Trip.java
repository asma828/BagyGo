package com.bagygo.bagygo_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String departureCity;
    private String arrivalCity;

    private LocalDateTime departureDate;
    private LocalDateTime arrivalDate;

    private Double availableSpace;
    private Double availableWeight;


    @OneToOne(mappedBy = "trip")
    private TransportOffer offer;

    @OneToMany
    private List<Location> locations;
}

