package com.barraiser.onboarding.interview;

import com.barraiser.onboarding.payment.expert.InterviewServiceDeskProcessingData;

import java.io.IOException;

public interface InterviewServiceDeskEventProcessor {
    void process(InterviewServiceDeskProcessingData data) throws Exception;
}
