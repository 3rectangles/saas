package com.barraiser.common.graphql.types;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CustomizableEvaluation {
    private String id;
    private List<SkillScore> scores;
    private List<InterviewForCustomizableEvaluation> interviews;
}

