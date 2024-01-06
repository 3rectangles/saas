package com.barraiser.onboarding.payment;

import com.barraiser.onboarding.dal.PaymentDAO;
import com.barraiser.onboarding.dal.PaymentRepository;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.common.graphql.input.BgsEnquiryInput;
import com.barraiser.onboarding.interview.BgsEnquiryManager;
import com.razorpay.RazorpayClient;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Log4j2
@Component
@AllArgsConstructor
public class CreatePaymentOrderForBGSMutation implements NamedDataFetcher<Object> {
    private final RazorpayClient razorpayClient;
    private final PaymentRepository paymentRepository;
    private final BgsEnquiryManager bgsEnquiryManager;
    private final GraphQLUtil graphQLUtil;
    private final PaymentManager paymentManager;

    @Qualifier("applicationEnvironemnt")
    private final String applicationEnvironment;

    @Override
    public Object get(final DataFetchingEnvironment environment) throws Exception {
        final BgsEnquiryInput input = this.graphQLUtil.getInput(environment, BgsEnquiryInput.class);

        if (input.getInterested().equals(Boolean.FALSE)) {
            return null;
        }

        log.info("environment value {}", this.applicationEnvironment);

        PaymentDAO paymentToReturn = PaymentDAO.builder()
            .id(UUID.randomUUID().toString())
            .userId(input.getCandidateId())
            .paymentId(UUID.randomUUID().toString())
            .amount(bgsEnquiryManager.getCandidateBgsCost().getValue())
            .currency("INR") //TODO: Add in app-config
            .type("BGS-ENQUIRY")
            .build();

        final String orderId = paymentManager.createOrderInRazorPay(paymentToReturn);
        paymentToReturn = paymentToReturn.toBuilder()
            .orderId(orderId)
            .status(PaymentStatus.ORDER_CREATED)
            .build();

        this.paymentRepository.save(paymentToReturn);

        log.info("Returning payment: {}", paymentToReturn);

        return DataFetcherResult.newResult()
            .data(paymentToReturn)
            .build();
    }

    @Override
    public String name() {
        return "createPaymentOrderForBGS";
    }

    @Override
    public String type() {
        return MUTATION_TYPE;
    }
}
