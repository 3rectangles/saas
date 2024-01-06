package com.barraiser.common.graphql.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CreateIntroductoryInterviewInput {
    /**
     * Start date of the interview slot.
     */
    private long startDate;
    /**
     * End date of the interview slot
     */
    private long endDate;

    private String interviewerId;

    /**
     * Dream companies
     */
    private List<String> companies;

    /**
     * Desired role
     */
    private String desiredRole;

    /**
     * Tentative application start period.
     */
    private String timeToStartApplications;

    /**
     * Skill ids to focus on.
     */
    private List<String> skillsToFocus;

    /**
     * Dream job attributes
     */
    private DreamJobAttributes dreamJobAttributes;
}
