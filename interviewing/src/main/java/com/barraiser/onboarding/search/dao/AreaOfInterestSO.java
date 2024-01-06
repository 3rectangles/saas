package com.barraiser.onboarding.search.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AreaOfInterestSO {
    private String id;
    private String name;
    private String domain;
}
