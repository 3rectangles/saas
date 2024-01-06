package com.barraiser.onboarding.common;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public final class IdNameField {
    private String id;
    private String name;
}
