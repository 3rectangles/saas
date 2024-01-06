package com.barraiser.onboarding.payment;

import com.amazonaws.services.stepfunctions.AWSStepFunctions;
import com.amazonaws.services.stepfunctions.model.StartExecutionRequest;
import com.barraiser.onboarding.communication.PaymentCommunicationService;
import com.barraiser.onboarding.dal.PaymentDAO;
import com.barraiser.onboarding.dal.PaymentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@AllArgsConstructor
@Log4j2
public class PaymentService {
    public static final String STEP_FUNCTION_NAME = "arn:aws:states:ap-south-1:969111487786:stateMachine:%s-post-payment-processing";
    public static final String PAYMENT_CAPTURED_STATUS = "captured";
    public static final String PAYMENT_CAPTURED_EVENT = "payment.captured";
    public static final String PAYMENT_AUTHORIZED_STATUS = "authorized";
    public static final String PAYMENT_AUTHORIZED_EVENT = "payment.authorized";
    public static final String ORDER_SUCCESSFUL_STATUS = "paid";
    public static final String ORDER_SUCCESSFUL_EVENT = "order.paid";
    public static final String PAYMENT_FAILED_STATUS = "failed";
    public static final String PAYMENT_FAILED_EVENT = "payment.failed";
    private final AWSStepFunctions awsStepFunctions;
    private final Environment environment;
    private final PaymentRepository paymentRepository;
    private final ObjectMapper objectMapper;
    private final PaymentCommunicationService paymentCommunicationService;

    @Qualifier("RazorPayWebhookSecret")
    private final String razorPayWebHookSecret;

    public void verifySignatureAndStartSFNWorkflow(final String payload, final String razorPaySignature) throws RazorpayException {
        Utils.verifySignature(payload, razorPaySignature, this.razorPayWebHookSecret);

        this.awsStepFunctions.startExecution(new StartExecutionRequest()
            .withStateMachineArn(String.format(STEP_FUNCTION_NAME, this.environment.getActiveProfiles()[0]))
            .withInput(payload));
    }

    public void verifySignatureAndUpdateStatus(final String payload, final String razorPaySignature) throws RazorpayException, IOException {
        Utils.verifySignature(payload, razorPaySignature, this.razorPayWebHookSecret);

        final JsonNode node = this.objectMapper.readTree(payload);
        final String event = node.get("event").textValue();
        final JsonNode payment = node.get("payload").get("payment");

        if(payment!=null) {
            final String paymentId = payment.get("entity").get("id").textValue();
            final String orderId = payment.get("entity").get("order_id").textValue();
            final String status = payment.get("entity").get("status").textValue();

            switch (event) {
                case ORDER_SUCCESSFUL_EVENT:
                    this.updatePaymentStatusForOrder(orderId, paymentId, PaymentStatus.fromString(ORDER_SUCCESSFUL_STATUS));
                    this.paymentCommunicationService.sendConfirmationEmail(paymentId);
                    break;
                case PAYMENT_CAPTURED_EVENT:
                    this.updatePaymentStatusForOrder(orderId, paymentId, PaymentStatus.fromString(PAYMENT_CAPTURED_STATUS));
                    break;
                case PAYMENT_AUTHORIZED_EVENT:
                    this.updatePaymentStatusForOrder(orderId, paymentId, PaymentStatus.fromString(PAYMENT_AUTHORIZED_STATUS));
                    break;
                case PAYMENT_FAILED_EVENT:
                    this.updatePaymentStatusForOrder(orderId, paymentId, PaymentStatus.fromString(PAYMENT_FAILED_STATUS));
                    break;
            }
        }
    }

    public void updatePaymentStatusForOrder(final String orderId, final String razorPayPaymentId, final PaymentStatus paymentStatus) {
        PaymentDAO savedPayment = paymentRepository.findByOrderId(orderId);
        savedPayment = savedPayment.toBuilder()
            .razorpayPaymentId(razorPayPaymentId)
            .status(paymentStatus)
            .build();
        paymentRepository.save(savedPayment);
    }

    public void updatePaymentStatusForPayment(final String paymentId, final PaymentStatus paymentStatus) {
        PaymentDAO savedPayment = paymentRepository.findByPaymentId(paymentId);
        savedPayment = savedPayment.toBuilder()
            .status(paymentStatus)
            .build();
        paymentRepository.save(savedPayment);
    }
}
