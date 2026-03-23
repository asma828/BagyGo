package com.bagygo.bagygo_backend.controller;

import com.bagygo.bagygo_backend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/process/{requestId}")
    public ResponseEntity<?> processPayment(@PathVariable("requestId") Long requestId) {
        try {
            Map<String, String> sessionData = paymentService.processPayment(requestId);
            return ResponseEntity.ok(sessionData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            paymentService.handleStripeWebhook(payload, sigHeader);
            return ResponseEntity.ok("Success");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Webhook Error: " + e.getMessage());
        }
    }

    @GetMapping("/verify/{sessionId}")
    public ResponseEntity<?> verifyPayment(@PathVariable("sessionId") String sessionId) {
        try {
            paymentService.verifySession(sessionId);
            return ResponseEntity.ok(Map.of("message", "Payment verified and updated"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
