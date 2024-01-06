package com.barraiser.onboarding.interview.search;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

//@Component
@AllArgsConstructor
public class TargetRoleAndExperienceBasedMatcher implements InterviewerMatcher {
    private final DynamoDBMapper dynamoDBMapper;

    @Override
    public List<String> getInterviewers(final Map<String, String> parameters) {

        return null;
    }
}
