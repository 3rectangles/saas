package com.barraiser.onboarding.jobRoleManagement.JobRole.graphql.input;

import com.barraiser.common.graphql.input.SearchInput;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class SearchJobRoleInput {
    Boolean onlyAllowLatestVersion;

    String partnerId;

    SearchInput searchJobRoleQuery;
}
