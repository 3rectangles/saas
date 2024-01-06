package com.barraiser.onboarding.interview.jira.dto;

import com.barraiser.onboarding.common.IdNameField;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
public class IdLinkedIssueField {
    private String id;
    private String name;
    private IdNameField type;
    private EvaluationServiceDeskIssue inwardIssue;
}
