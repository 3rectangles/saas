package com.barraiser.onboarding.graphql.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UpdateExpertSpecificSkillInput {
    private String expertId;
    private String skillId;
    private Double proficiency;
}
