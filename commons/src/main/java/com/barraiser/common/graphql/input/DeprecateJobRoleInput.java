package com.barraiser.common.graphql.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class DeprecateJobRoleInput {
    private String jobRoleId;
    private Integer jobRoleVersion;
}