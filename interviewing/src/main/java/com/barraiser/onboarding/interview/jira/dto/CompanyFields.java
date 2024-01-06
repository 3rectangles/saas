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
public class CompanyFields {
    private String id;
    private String key;
    private Fields fields;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder(toBuilder = true)
    public static class Fields {
        @JsonProperty("customfield_10188")
        private String companyName;

        @JsonProperty("customfield_10214")
        private String companyLogoUrl;

        @JsonProperty("customfield_10190")
        private String companyWebsite;

        @JsonProperty("customfield_10213")
        private IdValueField companyIndustry;

        @JsonProperty("customfield_10179")
        private IdValueField companySize;
    }
}
