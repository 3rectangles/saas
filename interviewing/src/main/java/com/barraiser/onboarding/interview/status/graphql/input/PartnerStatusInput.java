package com.barraiser.onboarding.interview.status.graphql.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PartnerStatusInput {
    private String statusId;
    private String evaluationId;
}
