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
public class PartnerCompanyIssue {
    private String id;
    private String key;
    private Fields fields;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder(toBuilder = true)
    public static class Fields {

        @JsonProperty("customfield_10225")
        private IdValueField company;

        @JsonProperty("customfield_10173")
        private String hrEmail;

        @JsonProperty("customfield_10227")
        private String hrPhone;

        @JsonProperty("customfield_10175")
        private String escalationEmail;

        @JsonProperty("customfield_10145")
        private String industry;

    }
}
