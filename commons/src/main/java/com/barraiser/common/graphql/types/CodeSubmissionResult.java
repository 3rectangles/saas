package com.barraiser.common.graphql.types;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CodeSubmissionResult {
    private Boolean isCompleted;
    private Boolean isSuccessful;
    private String time;
    private String memory;
    private String status;
    private String error;
    private String output;
}
