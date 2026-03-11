package com.bagygo.bagygo_backend.entity;

import com.bagygo.bagygo_backend.enums.TripStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "trips")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transporter_id", nullable = false)
    private User transporter;

    @Column(nullable = false)
    private String departureCity;

    @Column(nullable = false)
    private String arrivalCity;

    @Column(nullable = false)
    private LocalDateTime departureDate;

    private LocalDateTime estimatedArrival;

    @Column(nullable = false)
    private Double availableSpace;   // kg

    @Column(nullable = false)
    private Double pricePerKg;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TripStatus status = TripStatus.OPEN;

    private String notes;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
//---
//        package com.bagygo.bagygo_backend.dto.response;
//
//import com.bagygo.bagygo_backend.entity.Trip;
//import com.bagygo.bagygo_backend.enums.TripStatus;
//import lombok.Data;
//
//import java.time.LocalDateTime;
//
//@Data
//public class TripResponse {
//    private Long id;
//    private UserResponse transporter;
//    private String departureCity;
//    private String arrivalCity;
//    private LocalDateTime departureDate;
//    private LocalDateTime estimatedArrival;
//    private Double availableSpace;
//    private Double pricePerKg;
//    private TripStatus status;
//    private String notes;
//    private LocalDateTime createdAt;
//
//    public static TripResponse from(Trip t) {
//        TripResponse r = new TripResponse();
//        r.setId(t.getId());
//        r.setTransporter(UserResponse.from(t.getTransporter()));
//        r.setDepartureCity(t.getDepartureCity());
//        r.setArrivalCity(t.getArrivalCity());
//        r.setDepartureDate(t.getDepartureDate());
//        r.setEstimatedArrival(t.getEstimatedArrival());
//        r.setAvailableSpace(t.getAvailableSpace());
//        r.setPricePerKg(t.getPricePerKg());
//        r.setStatus(t.getStatus());
//        r.setNotes(t.getNotes());
//        r.setCreatedAt(t.getCreatedAt());
//        return r;
//    }
//}
//---
//        package com.bagygo.bagygo_backend.dto.request;
//
//import jakarta.validation.constraints.*;
//        import lombok.Data;
//
//import java.time.LocalDateTime;
//
//@Data
//public class CreateTripRequest {
//    @NotBlank  private String departureCity;
//    @NotBlank  private String arrivalCity;
//    @NotNull   @Future  private LocalDateTime departureDate;
//    private LocalDateTime estimatedArrival;
//    @NotNull   @Positive private Double availableSpace;
//    @NotNull   @Positive private Double pricePerKg;
//    private String notes;
//}
//---
//        package com.bagygo.bagygo_backend.enums;
//
//public enum TripStatus {
//    OPEN,
//    FULL,
//    IN_PROGRESS,
//    COMPLETED,
//    CANCELLED
//}