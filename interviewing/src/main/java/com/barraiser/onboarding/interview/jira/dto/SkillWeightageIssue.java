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
public class SkillWeightageIssue {
    private String id;
    private String key;
    private Fields fields;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder(toBuilder = true)
    public static class Fields {

        @JsonProperty("customfield_10192")
        private IdValueField evaluationId;

        @JsonProperty("customfield_10232")
        private IdValueField skill;

        @JsonProperty("customfield_10231")
        private IdValueField jobRole;

        @JsonProperty("customfield_10233")
        private Double weightage;
    }
}
