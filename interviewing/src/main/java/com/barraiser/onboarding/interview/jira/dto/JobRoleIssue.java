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
public class JobRoleIssue {
    private String id;
    private String key;
    private Fields fields;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder(toBuilder = true)
    public static class Fields {

        @JsonProperty("parent")
        private PartnerCompanyIssue parent;

        @JsonProperty("customfield_10176")
        private String roleName;

        @JsonProperty("customfield_10178")
        private IdValueField category;

        @JsonProperty("customfield_10177")
        private IdValueField domain;

        @JsonProperty("customfield_10228")
        private Integer minExperience;

        @JsonProperty("customfield_10229")
        private Integer maxExperience;
    }
}
