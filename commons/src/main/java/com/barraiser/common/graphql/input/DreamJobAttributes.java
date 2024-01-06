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
public class DreamJobAttributes {
    private List<String> companies;
    private String desiredRole;
    //    private String tentativeApplicationStartPeriod;
    private String timeToStartApplications;
    private List<String> skillsToFocus;
}
