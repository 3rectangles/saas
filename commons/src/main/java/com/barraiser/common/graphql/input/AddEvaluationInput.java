package com.barraiser.common.graphql.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AddEvaluationInput {
    private String candidateName;
    private String resumeUrl;
    private String jobRoleId;
    private String instructions;
    private String pocEmail;
}
