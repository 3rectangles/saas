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
public class InterviewForCustomizableEvaluation {
    private String id;
    private Interviewer interviewer;
    private Interviewee interviewee;
    private List<String> skillIds;
    private String interviewRound;
    private InterviewStructure interviewStructure;
    private Long startDate;
    private Long actualStartDate;
    private Long endDate;
    private Long actualEndDate;
    private Long lastQuestionEnd;
    private String youtubeLink;
    private String interviewStructureName;
    private String category;
    private String domainId;
    private String videoLink;
    private List<Question> questions;
    private OverallFeedback overallFeedback;
}
