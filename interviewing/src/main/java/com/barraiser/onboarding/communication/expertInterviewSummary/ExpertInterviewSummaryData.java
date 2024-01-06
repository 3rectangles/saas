package com.barraiser.onboarding.communication.expertInterviewSummary;

import com.barraiser.onboarding.dal.InterviewDAO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ExpertInterviewSummaryData {

    private String interviewerId;
    private Integer completedInterviews;
    private Integer cancelledInterviews;
    private Integer lastMinuteCancelledInterviews;
    private Map<String, Integer> countPerCancellationReason;
    private List<InterviewDAO> interviewsOfExpert;
    private String template;
}
