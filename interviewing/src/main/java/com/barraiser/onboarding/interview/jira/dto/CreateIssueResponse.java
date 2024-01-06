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
public class CreateIssueResponse {
    private String id;
    private String key;
    private Transition transition;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder(toBuilder = true)
    public static class Transition {
        @JsonProperty("status")
        private Integer status;

        @JsonProperty("errorMessages")
        private List<String> errorMessages;
    }
}
