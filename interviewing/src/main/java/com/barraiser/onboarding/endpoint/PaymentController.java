/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.endpoint;

import com.barraiser.onboarding.common.StaticAppConfigValues;
import com.barraiser.onboarding.payment.PaymentService;
import com.barraiser.onboarding.payment.PaymentStatus;
import com.barraiser.onboarding.payment.dto.PaymentCallBackDTO;
import com.razorpay.RazorpayException;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Used for creating a web-hook.
 */
@Log4j2
@RestController
@AllArgsConstructor
public class PaymentController {
	private final PaymentService paymentService;
	private final StaticAppConfigValues staticAppConfigValues;

	@Qualifier("RazorPayWebhookSecret")
	private final String razorPayWebHookSecret;

	/**
	 * Webhook to receive notification of events of payment events from RazorPay.
	 */
	@PostMapping("/payment/notification")
	public void receivePaymentNotification(@RequestBody final String rawRequest,
			@RequestHeader("X-Razorpay-Signature") final String razorPaySignature)
			throws IOException, RazorpayException {

		log.info("Received notification: " + rawRequest);
		// this.paymentService.verifySignatureAndStartSFNWorkflow(rawRequest,
		// razorPaySignature);
		this.paymentService.verifySignatureAndUpdateStatus(rawRequest, razorPaySignature);
	}

	/**
	 * Frontend callback from razorpay for a success/failure payment.
	 */
	@PostMapping(value = "/payment/callback", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public void handleFrontendCallback(final PaymentCallBackDTO payload,
			@RequestParam final String paymentId,
			final HttpServletResponse response) throws IOException {
		// TODO: Verify signature.
		// Utils.verifySignature(, payload.getRazorpaySignature(),
		// this.razorPayWebHookSecret);
		// log.info("success for payment id {}: {}", paymentId,
		// payload.getRazorpayPaymentId());
		this.paymentService.updatePaymentStatusForPayment(paymentId, PaymentStatus.PAID);
		response.sendRedirect(String.format("http://%s/payment-status?paymentId=%s",
				this.staticAppConfigValues.getPaymentRedirectionHost(), paymentId));
	}

	/**
	 * Frontend callback from razorpay for a user cancelled payment.
	 */
	@PostMapping(value = "/payment/user-cancellation-callback")
	public void handleUserCancelCallback(@RequestParam(value = "paymentId", required = false) final String paymentId)
			throws IOException {
		log.info("user canceled for payment id {}", paymentId);
		this.paymentService.updatePaymentStatusForPayment(paymentId, PaymentStatus.CANCELED);
	}
}
