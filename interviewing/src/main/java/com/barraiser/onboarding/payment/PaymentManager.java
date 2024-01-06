package com.barraiser.onboarding.payment;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.barraiser.onboarding.dal.PaymentDAO;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Log4j2
@AllArgsConstructor
public class PaymentManager {
    private final DynamoDBMapper dynamoDBMapper;
    private final RazorpayClient razorpayClient;

    @Qualifier("applicationEnvironemnt")
    private final String applicationEnvironment;


    public void updatePayment(final PaymentDAO payment) {
        this.dynamoDBMapper.save(payment);
    }

    public String createOrderInRazorPay(PaymentDAO payment) throws RazorpayException, JSONException {
        log.info("Creating an order in Razor pay payment-id: {}", payment.getPaymentId());
        final JSONObject orderRequest = new JSONObject();


        // we need to add the amount to the precision of smallest unit of a currency e.g. paisa for rupee, cent for dollar.
        orderRequest.put("amount", payment.getAmount() * 100);
        orderRequest.put("currency", payment.getCurrency());
        orderRequest.put("receipt", payment.getPaymentId()); // TODO: Check what does a receipt do?
        orderRequest.put("payment_capture", true);

        final Map<String, String> notes = new HashMap<>();
        notes.put("environment", this.applicationEnvironment);
        orderRequest.put("notes", notes);

        final Order order = this.razorpayClient.Orders.create(orderRequest);
        final String orderId = order.get("id");
        final String status = order.get("status");
        log.info("Razopay response :{}", order.toString());

        log.info("Received orderId from razorpay: {}", orderId);
        // TODO: Throw exception if order is not success.
        return orderId;
    }
}
