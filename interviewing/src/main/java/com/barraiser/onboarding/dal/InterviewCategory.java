package com.barraiser.onboarding.dal;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class InterviewCategory {
    private String id;
    private String name;
    private List<InterviewCategory> interviewCategories;
}
