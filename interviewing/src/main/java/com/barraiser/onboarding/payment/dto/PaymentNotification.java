package com.barraiser.onboarding.payment.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PaymentNotification {
    private String razorpayPaymentId;
    private String razorpayOrderId;
    private String keySecret;
    private Error error;

    @Data
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class Error {
        private String code;
        private String description;
        private String field;
        private String source;
        private String step;
        private String reason;
        private Metadata metadata;
    }

    @Data
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    private static class Metadata {
        /* Razor pay's payment id */
        private String paymentId;
        private String orderId;
    }
}
