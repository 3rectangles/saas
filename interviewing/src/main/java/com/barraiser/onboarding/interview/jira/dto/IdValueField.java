package com.barraiser.onboarding.interview.jira.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public final class IdValueField {
    private String value;
    private String id;
}
