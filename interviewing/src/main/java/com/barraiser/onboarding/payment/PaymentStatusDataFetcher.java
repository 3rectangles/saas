package com.barraiser.onboarding.payment;

import com.barraiser.onboarding.dal.PaymentDAO;
import com.barraiser.onboarding.dal.PaymentRepository;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.common.graphql.input.PaymentStatusInput;
import com.barraiser.onboarding.interview.BgsEnquiryManager;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class PaymentStatusDataFetcher implements NamedDataFetcher<DataFetcherResult<Object>> {
    final private BgsEnquiryManager bgsEnquiryManager;
    final private PaymentRepository paymentRepository;
    final private GraphQLUtil graphQLUtil;

    @Override
    public DataFetcherResult<Object> get(final DataFetchingEnvironment environment) throws Exception {

        PaymentStatusInput input = graphQLUtil.getInput(environment, PaymentStatusInput.class);

        PaymentDAO paymentDAO = this.paymentRepository.findByPaymentId(input.getPaymentId());

        return DataFetcherResult.newResult()
            .data(paymentDAO)
            .build();
    }

    @Override
    public String name() {
        return "getPaymentStatus";
    }

    @Override
    public String type() {
        return QUERY_TYPE;
    }
}
