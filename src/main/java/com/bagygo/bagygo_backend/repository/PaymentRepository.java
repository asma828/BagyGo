package com.bagygo.bagygo_backend.repository;

import com.bagygo.bagygo_backend.entity.BaggageRequest;
import com.bagygo.bagygo_backend.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByBaggageRequest(BaggageRequest baggageRequest);
    Optional<Payment> findByStripePaymentIntentId(String stripePaymentIntentId);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(p.amount), 0.0) FROM Payment p WHERE p.status = 'PAID'")
    double sumTotalRevenue();
}
