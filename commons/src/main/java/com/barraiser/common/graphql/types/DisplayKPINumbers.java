package com.barraiser.common.graphql.types;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class DisplayKPINumbers {

    private int numberOfInterviews;
    private int numberOfExperts;
    private int numberOfPartners;

}
