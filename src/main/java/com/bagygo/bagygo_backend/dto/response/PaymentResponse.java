package com.bagygo.bagygo_backend.dto.response;

import com.bagygo.bagygo_backend.entity.Payment;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentResponse {
    private Long id;
    private Double amount;
    private String status;
    private String paymentMethod;
    private LocalDateTime createdAt;
    // We can include request info if needed, but simple fields are safest
    private Long requestId;

    public static PaymentResponse from(Payment p) {
        if (p == null) return null;
        PaymentResponse r = new PaymentResponse();
        r.setId(p.getId());
        r.setAmount(p.getAmount());
        r.setStatus(p.getStatus());
        r.setPaymentMethod(p.getPaymentMethod());
        r.setCreatedAt(p.getCreatedAt());
        if (p.getBaggageRequest() != null) {
            r.setRequestId(p.getBaggageRequest().getId());
        }
        return r;
    }
}
