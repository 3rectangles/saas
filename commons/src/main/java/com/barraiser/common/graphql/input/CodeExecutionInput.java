package com.barraiser.common.graphql.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CodeExecutionInput {

    private String interviewId;

    private String sourceCode;

    private Integer compilerId;

    private Integer compilerVersionId;
}
