package com.barraiser.onboarding.interview.jira.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class InterviewRoundIssue {
    private String id;
    private String key;
    private Fields fields;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Fields {

        @JsonProperty("parent")
        private JobRoleIssue parent;

        @JsonProperty("customfield_10143")
        private IdValueField interviewStructure;

        @JsonProperty("customfield_10230")
        private IdValueField roundType;

    }
}
