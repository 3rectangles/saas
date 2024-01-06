package com.barraiser.common.graphql.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CandidateCompensationCalculatorInput {
    private String domainId;
    private Integer workExperience;
    private Double currentCTC;
    private String userIdentity;
}
