package com.barraiser.onboarding.interview.jira.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class SkillIssue {
    private String id;
    private String key;
    private Fields fields;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder(toBuilder = true)
    public static class Fields {

        @JsonProperty("customfield_10186")
        private String skillName;

        @JsonProperty("customfield_10191")
        private IdValueField parentSkill;

        @JsonProperty("customfield_10218")
        private IdValueField domain;

        @JsonProperty("customfield_10181")
        private List<IdValueField> time;
    }
}
