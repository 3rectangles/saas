package com.barraiser.onboarding.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
public class PaymentCallBackDTO {
    @JsonProperty("razorpay_payment_id")
    private String razorpayPaymentId;
    @JsonProperty("razorpay_order_id")
    private String razorpayOrderId;
    @JsonProperty("razorpay_signature")
    private String razorpaySignature;
}
