package com.bagygo.bagygo_backend.service;

import com.bagygo.bagygo_backend.entity.User;
import com.bagygo.bagygo_backend.enums.RequestStatus;
import com.bagygo.bagygo_backend.enums.UserRole;
import com.bagygo.bagygo_backend.repository.BaggageRequestRepository;
import com.bagygo.bagygo_backend.repository.PaymentRepository;
import com.bagygo.bagygo_backend.repository.TripRepository;
import com.bagygo.bagygo_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final UserRepository userRepository;
    private final TripRepository tripRepository;
    private final BaggageRequestRepository requestRepository;
    private final PaymentRepository paymentRepository;

    // ── User Management ───────────────────────────────────
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Page<User> searchUsers(String query, Pageable pageable) {
        return userRepository.searchUsers(query, pageable);
    }

    public void updateUserStatus(Long userId, boolean isBanned) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setIsBanned(isBanned);
        userRepository.save(user);
    }

    // ── Transporter Verification ──────────────────────────
    public Page<User> getPendingVerifications(Pageable pageable) {
        // For simplicity, we assume transporters needing verification are not verified yet
        return userRepository.findByRoleIn(Arrays.asList(UserRole.TRANSPORTEUR), pageable);
    }

    public void verifyTransporter(Long userId, boolean approve) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (user.getRole() != UserRole.TRANSPORTEUR) {
            throw new IllegalArgumentException("User is not a transporter");
        }
        user.setIsVerified(approve);
        userRepository.save(user);
    }

    // ── Monitoring ────────────────────────────────────────
    public Page<com.bagygo.bagygo_backend.dto.response.TripResponse> getAllTrips(Pageable pageable) {
        return tripRepository.findAll(pageable)
                .map(com.bagygo.bagygo_backend.dto.response.TripResponse::from);
    }

    public Page<com.bagygo.bagygo_backend.dto.response.BaggageRequestResponse> getAllRequests(Pageable pageable) {
        return requestRepository.findAll(pageable)
                .map(com.bagygo.bagygo_backend.dto.response.BaggageRequestResponse::from);
    }

    public Page<com.bagygo.bagygo_backend.dto.response.PaymentResponse> getAllPayments(Pageable pageable) {
        return paymentRepository.findAll(pageable)
                .map(com.bagygo.bagygo_backend.dto.response.PaymentResponse::from);
    }

    // ── Platform Statistics ───────────────────────────────
    public Map<String, Object> getPlatformStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalTrips", tripRepository.count());
        stats.put("totalDeliveries", requestRepository.countByStatus(RequestStatus.COMPLETED));
        stats.put("totalRevenue", paymentRepository.sumTotalRevenue());
        
        System.out.println("DEBUG: Platform Stats calculated: " + stats);
        return stats;
    }
}
