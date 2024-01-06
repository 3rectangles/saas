package com.barraiser.onboarding.communication;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AdminGetUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminGetUserResult;
import com.barraiser.onboarding.common.StaticAppConfigValues;
import com.barraiser.onboarding.communication.channels.email.EmailService;
import com.barraiser.onboarding.dal.PaymentDAO;
import com.barraiser.onboarding.dal.PaymentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class PaymentCommunicationService {
    public static final String PAYMENT_CONFIRMATION_SUBJECT = "Thank you for your interest in the BGS report";
    private final EmailService emailService;
    private final PaymentRepository paymentRepository;
    private final AWSCognitoIdentityProvider awsCognitoIdentityProvider;
    private final StaticAppConfigValues staticAppConfigValues;

    /**
     * Finds the payment and user related to the orderId and sends a confirmation email.
     *
     *
     * @param paymentId paymentId against which interviews were booked.
     * @throws IOException
     */
    public void sendConfirmationEmail(final String paymentId) throws IOException {
        final PaymentDAO payment = this.paymentRepository.findByRazorpayPaymentId(paymentId);
        // TODO: encapsulate these two to return a complete user.
        final AdminGetUserResult response = this.awsCognitoIdentityProvider.adminGetUser(new AdminGetUserRequest()
            .withUsername(payment.getUserId())
            .withUserPoolId(this.staticAppConfigValues.getUserPoolId()));
        final String toEmail = response.getUserAttributes().stream()
            .filter(x -> "email".equals(x.getName()))
            .findFirst()
            .get().getValue();

        this.emailService.sendEmail(this.staticAppConfigValues.getInterviewLifecycleInformationEmail(), PAYMENT_CONFIRMATION_SUBJECT,
            "payment_success_template", List.of(toEmail),
            List.of(this.staticAppConfigValues.getInterviewLifecycleInformationEmail()), new HashMap<>(), null);
    }
}
