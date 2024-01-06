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
public class InterviewStructureIssue {
    private String id;
    private String key;
    private Fields fields;


    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder(toBuilder = true)
    public static class Fields {

        @JsonProperty("customfield_10187")
        private String structureName;

        @JsonProperty("customfield_10220")
        private Integer minExperience;

        @JsonProperty("customfield_10221")
        private Integer maxExperience;

        @JsonProperty("customfield_10218")
        private IdValueField domainId;

        @JsonProperty("customfield_10183")
        private IdValueField interviewRoundType;

        @JsonProperty("customfield_10184")
        private String googleSheetLink;

        @JsonProperty("customfield_10182")
        private List<IdValueField> skills;

        @JsonProperty("customfield_10185")
        private List<IdValueField> skillsToBeFocussedOn;
    }
}
