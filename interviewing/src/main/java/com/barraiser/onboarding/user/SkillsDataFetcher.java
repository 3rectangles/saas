package com.barraiser.onboarding.user;

import com.barraiser.onboarding.dal.SkillDAO;
import com.barraiser.onboarding.dal.SkillRepository;
import com.barraiser.onboarding.dal.TargetJobAttributesDAO;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class SkillsDataFetcher implements NamedDataFetcher {
    private final SkillRepository skillRepository;

    @Override
    public String name() {
        return "skillsToFocus";
    }

    @Override
    public String type() {
        return "TargetJobAttributes";
    }

    @Override
    public Object get(final DataFetchingEnvironment environment) throws Exception {
        final TargetJobAttributesDAO targetJobAttributesDAO = environment.getSource();
        final List<SkillDAO> skills = this.skillRepository.findAllByIdInAndDomainIsNull(targetJobAttributesDAO.getSkillsToFocus());
        return DataFetcherResult.newResult()
                .data(skills)
                .build();
    }
}
