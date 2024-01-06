package com.barraiser.common.graphql.input;

import com.barraiser.common.graphql.types.Skill;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class SkillWeightageInput {
    private Skill skill;
    private Double weightage;
}
