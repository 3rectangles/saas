package com.barraiser.onboarding.payment.expert;

import com.barraiser.onboarding.dal.ExpertDAO;
import com.barraiser.onboarding.dal.InterviewDAO;
import lombok.Data;

@Data
public class InterviewServiceDeskProcessingData {
    private InterviewDAO interview;
}
