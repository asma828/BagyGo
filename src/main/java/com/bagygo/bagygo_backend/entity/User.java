package com.bagygo.bagygo_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    @Column(unique = true)
    private String email;

    private String password;
    private String phone;
    private Double rating;

    private LocalDateTime createdAt;

    @ManyToOne
    private Role role;

    @OneToMany(mappedBy = "user")
    private List<BaggageRequest> baggageRequests;

    @OneToMany(mappedBy = "user")
    private List<TransportOffer> transportOffers;

    @OneToMany(mappedBy = "user")
    private List<Message> messages;

    @OneToMany(mappedBy = "user")
    private List<Rating> ratings;
}
