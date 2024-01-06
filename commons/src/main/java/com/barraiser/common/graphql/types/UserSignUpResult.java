package com.barraiser.common.graphql.types;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserSignUpResult {
    private boolean authenticated;
    private boolean success;
}
