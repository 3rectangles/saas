package com.barraiser.onboarding.interview.jira.dto;

import lombok.Data;

@Data
public final class Person {
    private String displayName;
    private String emailAddress;
    private String accountId;
    private String accountType;
}
