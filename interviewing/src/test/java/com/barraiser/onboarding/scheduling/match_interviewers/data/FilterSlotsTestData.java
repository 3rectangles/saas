package com.barraiser.onboarding.scheduling.match_interviewers.data;

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
public class FilterSlotsTestData {

    private Map<Long, String> slotsToInterviewer;
    private Map<Long, String> expectedSlotsToInterviewer;
    private List<InterviewDAO> interviewsOfInterviewee;
}
