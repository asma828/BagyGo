package com.bagygo.bagygo_backend.service;

import com.bagygo.bagygo_backend.entity.BaggageRequest;
import com.bagygo.bagygo_backend.entity.Payment;
import com.bagygo.bagygo_backend.enums.RequestStatus;
import com.bagygo.bagygo_backend.repository.BaggageRequestRepository;
import com.bagygo.bagygo_backend.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BaggageRequestRepository baggageRequestRepository;
    private final NotificationService notificationService;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    @Transactional
    public Map<String, String> processPayment(Long requestId) {
        BaggageRequest request = baggageRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (request.getStatus() != RequestStatus.ACCEPTED && 
            request.getStatus() != RequestStatus.IN_TRANSIT && 
            request.getStatus() != RequestStatus.DELIVERED) {
            throw new RuntimeException("Request must be accepted before payment (Current status: " + request.getStatus() + ")");
        }

        if (request.getIsPaid()) {
            throw new RuntimeException("Request is already paid");
        }

        try {
            // Note: In production you should pass the frontend url via environment
            // variables
            String frontendUrl = "http://localhost:4200";

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(frontendUrl + "/dashboard/sender/payment/success?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl(frontendUrl + "/dashboard/sender/payment/cancel")
                    .putMetadata("requestId", String.valueOf(requestId))
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("eur")
                                                    .setUnitAmount((long) (request.getProposedPrice() * 100))
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName("BagyGo Transport - "
                                                                            + request.getDepartureCity() + " to "
                                                                            + request.getArrivalCity())
                                                                    .build())
                                                    .build())
                                    .build())
                    .build();

            Session session = Session.create(params);

            Payment payment = Payment.builder()
                    .baggageRequest(request)
                    .amount(request.getProposedPrice())
                    .status("PENDING")
                    .stripePaymentIntentId(session.getId())
                    .createdAt(LocalDateTime.now())
                    .build();

            paymentRepository.save(payment);

            Map<String, String> responseData = new HashMap<>();
            responseData.put("sessionId", session.getId());
            responseData.put("url", session.getUrl());
            return responseData;

        } catch (Exception e) {
            throw new RuntimeException("Stripe error: " + e.getMessage());
        }
    }

    @Transactional
    public void handleStripeWebhook(String payload, String sigHeader) {
        Event event = null;
        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            throw new RuntimeException("Invalid signature");
        }

        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            if (session != null) {
                String reqIdStr = session.getMetadata().get("requestId");
                if (reqIdStr != null) {
                    Long requestId = Long.parseLong(reqIdStr);
                    BaggageRequest request = baggageRequestRepository.findById(requestId).orElse(null);

                    if (request != null && !request.getIsPaid()) {
                        request.setIsPaid(true);
                        baggageRequestRepository.save(request);

                        Payment payment = paymentRepository.findByBaggageRequest(request).orElse(null);
                        if (payment != null) {
                            payment.setStatus("PAID");
                            payment.setPaymentMethod("STRIPE");
                            paymentRepository.save(payment);
                        }

                        // Send notifications
                        notificationService.createNotification(
                                request.getSender(),
                                "Votre paiement pour le trajet a été confirmé.",
                                com.bagygo.bagygo_backend.enums.NotificationType.SYSTEM);

                        if (request.getTrip() != null) {
                            notificationService.createNotification(
                                    request.getTrip().getTransporter(),
                                    "L'expéditeur a confirmé et payé le trajet. Vous pouvez commencer la livraison.",
                                    com.bagygo.bagygo_backend.enums.NotificationType.SYSTEM);
                        }
                    }
                }
            }
        }
    }

    @Transactional
    public void verifySession(String sessionId) {
        try {
            Session session = Session.retrieve(sessionId);
            System.out.println("Stripe Session Status: " + session.getStatus());
            System.out.println("Stripe Payment Status: " + session.getPaymentStatus());
            
            if ("paid".equals(session.getPaymentStatus()) || "complete".equals(session.getStatus())) {
                String reqIdStr = session.getMetadata().get("requestId");
                if (reqIdStr != null) {
                    Long requestId = Long.parseLong(reqIdStr);
                    System.out.println("Verifying payment for requestId: " + requestId);
                    BaggageRequest request = baggageRequestRepository.findById(requestId).orElse(null);

                    if (request != null) {
                        System.out.println("Request found: " + request.getId() + ", current isPaid: " + request.getIsPaid());
                        if (!request.getIsPaid()) {
                            request.setIsPaid(true);
                            baggageRequestRepository.save(request);
                            System.out.println("SET isPaid = true for request " + request.getId());

                            Payment payment = paymentRepository.findByBaggageRequest(request).orElse(null);
                            if (payment != null) {
                                payment.setStatus("PAID");
                                payment.setPaymentMethod("STRIPE");
                                paymentRepository.save(payment);
                                System.out.println("Payment record updated to PAID");
                            } else {
                                System.out.println("No Payment record found for request " + request.getId());
                            }
                        }
                    } else {
                        System.out.println("Request not found for id: " + requestId);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Stripe retrieval error: " + e.getMessage());
        }
    }
}
