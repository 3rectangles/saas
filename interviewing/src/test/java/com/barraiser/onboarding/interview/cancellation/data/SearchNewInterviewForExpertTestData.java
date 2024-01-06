package com.barraiser.onboarding.interview.cancellation.data;

import com.barraiser.onboarding.dal.ExpertDAO;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.InterviewToEligibleExpertsDAO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SearchNewInterviewForExpertTestData {

    private List<InterviewDAO> interviewsScheduledInSlot;
    private List<ExpertDAO> duplicateExperts;
    private InterviewDAO interviewThatExpertCanTake;
    private List<InterviewToEligibleExpertsDAO> interviewToEligibleExperts;
}
