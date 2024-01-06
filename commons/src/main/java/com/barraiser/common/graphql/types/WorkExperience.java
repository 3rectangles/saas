package com.barraiser.common.graphql.types;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum WorkExperience {
    ONE_TWO_YEARS,
    TWO_THREE_YEARS,
    THREE_FIVE_YEARS,
    FIVE_NINE_YEARS,
    NINE_TWELVE_YEARS,
    MORE_THAN_TWELEVE_YEARS;

    @JsonCreator

    public WorkExperience fromString(final String val) {
        return WorkExperience.valueOf(val.toUpperCase());
    }
}
