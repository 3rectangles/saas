package com.barraiser.onboarding.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PaymentDAOServiceTest {
    @Test
    public void testProcessNotification() throws JsonProcessingException {
        final String testString = "{\n" +
                "  \"razorpay_payment_id\": \"pay_29QQoUBi66xm2f\",\n" +
                "  \"razorpay_order_id\": \"order_9A33XWu170gUtm\",\n" +
                "  \"razorpay_signature\": \"9ef4dffbfd84f1318f6739a3ce19f9d85851857ae648f114332d8401e0949a3d\"\n" +
                "}";

//        final PaymentService paymentService = new PaymentServiceervice(new ObjectMapper());

//        paymentService.processPaymentNotification(testString);

    }

    @Test
    public void testJSONNODE() throws JsonProcessingException {
        final String payload = "{\n" +
                "  \"entity\": \"event\",\n" +
                "  \"account_id\": \"acc_F4qwSCxm7rBaLG\",\n" +
                "  \"event\": \"payment.authorized\",\n" +
                "  \"contains\": [\n" +
                "    \"payment\"\n" +
                "  ],\n" +
                "  \"payload\": {\n" +
                "    \"payment\": {\n" +
                "      \"entity\": {\n" +
                "        \"id\": \"pay_FAivq8JFMM18RB\",\n" +
                "        \"entity\": \"payment\",\n" +
                "        \"amount\": 1400000,\n" +
                "        \"currency\": \"INR\",\n" +
                "        \"status\": \"authorized\",\n" +
                "        \"order_id\": \"order_FAiR1tYIpFR9Ih\",\n" +
                "        \"invoice_id\": null,\n" +
                "        \"international\": false,\n" +
                "        \"method\": \"card\",\n" +
                "        \"amount_refunded\": 0,\n" +
                "        \"refund_status\": null,\n" +
                "        \"captured\": false,\n" +
                "        \"description\": \"Increase your chances of interview by 10X\",\n" +
                "        \"card_id\": \"card_FAivqDAEuETKD3\",\n" +
                "        \"card\": {\n" +
                "          \"id\": \"card_FAivqDAEuETKD3\",\n" +
                "          \"entity\": \"card\",\n" +
                "          \"name\": \"Ppppp\",\n" +
                "          \"last4\": \"1111\",\n" +
                "          \"network\": \"Visa\",\n" +
                "          \"type\": \"debit\",\n" +
                "          \"issuer\": null,\n" +
                "          \"international\": false,\n" +
                "          \"emi\": false\n" +
                "        },\n" +
                "        \"bank\": null,\n" +
                "        \"wallet\": null,\n" +
                "        \"vpa\": null,\n" +
                "        \"email\": \"prabhat.ranjan32@gmail.com\",\n" +
                "        \"contact\": \"+919538275444\",\n" +
                "        \"notes\": {\n" +
                "          \"shipping address\": \"Aban Essence, Bangalore-560068\"\n" +
                "        },\n" +
                "        \"fee\": null,\n" +
                "        \"tax\": null,\n" +
                "        \"error_code\": null,\n" +
                "        \"error_description\": null,\n" +
                "        \"error_source\": null,\n" +
                "        \"error_step\": null,\n" +
                "        \"error_reason\": null,\n" +
                "        \"created_at\": 1593932335\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  \"created_at\": 1593932339\n" +
                "}\n";
        final JsonNode node = new ObjectMapper().readTree(payload);
        final String orderId = node.get("payload").get("payment").get("entity").get("order_id").textValue();
        System.out.println(orderId);

    }
}