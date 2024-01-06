package com.barraiser.onboarding.user;

import com.barraiser.onboarding.dal.SkillWeightageRepository;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.common.graphql.types.JobRole;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SkillWeightageDataFetcher implements NamedDataFetcher {
    private final GraphQLUtil graphQLUtil;
    private final SkillWeightageRepository skillWeightageRepository;

    @Override
    public String name() {
        return "skillWeightages";
    }

    @Override
    public String type() {
        return "JobRole";
    }

    @Override
    public Object get(DataFetchingEnvironment environment) throws Exception {
        final JobRole jobRole = environment.getSource();
        final String jobRoleId = jobRole.getId();
        final Integer jobRoleVersion = jobRole.getVersion();
        return DataFetcherResult.newResult()
            .data(this.skillWeightageRepository.findAllByJobRoleIdAndJobRoleVersion(jobRoleId, jobRoleVersion))
            .build();

    }
}
