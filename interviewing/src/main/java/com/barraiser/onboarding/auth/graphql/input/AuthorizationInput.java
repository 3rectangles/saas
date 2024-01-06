package com.barraiser.onboarding.auth.graphql.input;

import com.barraiser.onboarding.auth.enums.Action;
import com.barraiser.common.graphql.input.ResourceAttribute;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class AuthorizationInput {
    private String resourceType;
    private Action action;
    private List<ResourceAttribute> resourceAttributes;
}
