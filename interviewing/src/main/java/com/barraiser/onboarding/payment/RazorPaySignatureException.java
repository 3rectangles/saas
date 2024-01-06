package com.barraiser.onboarding.payment;

public class RazorPaySignatureException extends IllegalArgumentException {
    public RazorPaySignatureException(final String badSignature) {
        super(badSignature);
    }
}
