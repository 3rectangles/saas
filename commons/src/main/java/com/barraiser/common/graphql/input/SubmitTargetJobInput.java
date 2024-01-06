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
public class SubmitTargetJobInput {
    private List<String> companies;
    private String desiredRole;
    private List<String> skillsToFocus;
    private String timeToStartApplications;
}
