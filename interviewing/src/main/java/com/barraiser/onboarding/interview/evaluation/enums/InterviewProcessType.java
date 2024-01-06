package com.barraiser.onboarding.interview.evaluation.enums;

public enum InterviewProcessType {
    BARRAISER("BARRAISER"),

    PARTNER_INTERNAL("PARTNER_INTERNAL"),

    OVERALL("OVERALL");
    private final String processType;

    InterviewProcessType(final String processType) {
        this.processType = processType;
    }

    public String getValue() {
        return this.processType;
    }

}
