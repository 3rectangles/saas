package com.barraiser.common.graphql.types;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AddBulkEvaluationsResult {
    private Boolean success;
    private String evaluationId;
    private String serialId;
    private List<AddBulkEvaluationsError> errors;
}
