package com.barraiser.onboarding.interview.jira.dto;

import com.barraiser.onboarding.common.IdNameField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Transition {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String status;

    @JsonProperty("to")
    private IdNameField toState;
}
