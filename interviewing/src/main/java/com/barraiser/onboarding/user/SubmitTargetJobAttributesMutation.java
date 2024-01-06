package com.barraiser.onboarding.user;

import com.barraiser.onboarding.dal.TargetJobAttributesDAO;
import com.barraiser.onboarding.dal.TargetJobAttributesRepository;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.common.graphql.input.SubmitTargetJobInput;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SubmitTargetJobAttributesMutation implements NamedDataFetcher {
    private final GraphQLUtil graphQLUtil;
    private final TargetJobAttributesRepository jobAttributesRepository;

    @Override
    public String name() {
        return "submitTargetJobAttributes";
    }

    @Override
    public String type() {
        return MUTATION_TYPE;
    }

    @Override
    public Object get(final DataFetchingEnvironment environment) throws Exception {
        final SubmitTargetJobInput input = this.graphQLUtil.getArgument(environment, "input", SubmitTargetJobInput.class);

        final String userName = this.graphQLUtil.getLoggedInUser(environment).getUserName();
        this.jobAttributesRepository.save(TargetJobAttributesDAO.builder()
                .userId(userName)
                .companies(input.getCompanies())
                .desiredRole(input.getDesiredRole())
                .skillsToFocus(input.getSkillsToFocus())
                .timeToStartApplications(input.getTimeToStartApplications())
                .build());

        return Boolean.TRUE;
    }
}
