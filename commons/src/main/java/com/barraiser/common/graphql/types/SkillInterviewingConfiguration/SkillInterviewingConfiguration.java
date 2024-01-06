package com.barraiser.common.graphql.types.SkillInterviewingConfiguration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SkillInterviewingConfiguration {

    private String id;

    private Integer version;

    private String domainId;

    private String skillId;

    private Integer duration;

    private String categoryCoverage;

    private String questioningType;

    private String sampleQuestions;

    private String mandatoryExpectations;

    private String barraisingExpectations;

}
