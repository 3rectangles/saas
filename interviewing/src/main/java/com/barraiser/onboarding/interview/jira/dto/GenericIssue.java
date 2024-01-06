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
public class GenericIssue {
    private String id;
    private String key;
    private Fields fields;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder(toBuilder = true)
    public static class Fields {
        private IdNameField issuetype;

        @JsonProperty("comment")
        private CommentField comment;
    }
}
