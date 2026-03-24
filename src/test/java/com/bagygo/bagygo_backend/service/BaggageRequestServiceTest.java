package com.bagygo.bagygo_backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bagygo.bagygo_backend.entity.BaggageRequest;
import com.bagygo.bagygo_backend.entity.Trip;
import com.bagygo.bagygo_backend.entity.User;
import com.bagygo.bagygo_backend.enums.NotificationType;
import com.bagygo.bagygo_backend.enums.RequestStatus;
import com.bagygo.bagygo_backend.repository.BaggageRequestRepository;
import com.bagygo.bagygo_backend.repository.TripRepository;

@ExtendWith(MockitoExtension.class)
public class BaggageRequestServiceTest {

    @Mock
    private BaggageRequestRepository repository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private BaggageRequestService baggageRequestService;

    private User transporter;
    private User sender;
    private Trip trip;
    private BaggageRequest baggageRequest;

    @BeforeEach
    void setUp() {
        transporter = User.builder().id(1L).build();
        sender = User.builder().id(2L).build();
        
        trip = Trip.builder().id(101L).transporter(transporter).build();
        
        baggageRequest = BaggageRequest.builder()
                .id(501L)
                .sender(sender)
                .trip(trip)
                .status(RequestStatus.ACCEPTED)
                .build();
    }

    @Test
    void shouldUpdateStatusToInTransit_WhenUserIsTransporter() {
        // Arrange
        when(repository.findById(501L)).thenReturn(Optional.of(baggageRequest));
        when(repository.save(any(BaggageRequest.class))).thenReturn(baggageRequest);

        // Act
        baggageRequestService.updateStatus(501L, RequestStatus.IN_TRANSIT, transporter);

        // Assert
        assertEquals(RequestStatus.IN_TRANSIT, baggageRequest.getStatus());
        verify(repository).save(baggageRequest);
        verify(notificationService).createNotification(eq(sender), anyString(), eq(NotificationType.SYSTEM));
    }

    @Test
    void shouldThrowException_WhenSenderTriesToSetInTransit() {
        // Arrange
        when(repository.findById(501L)).thenReturn(Optional.of(baggageRequest));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            baggageRequestService.updateStatus(501L, RequestStatus.IN_TRANSIT, sender);
        });
        
        verify(repository, never()).save(any());
    }
}
