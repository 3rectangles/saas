package com.barraiser.onboarding.expert.compensationPrediction;

import com.barraiser.onboarding.dal.ExpertCompensationCalculatorHistoryDAO;
import com.barraiser.onboarding.expert.ExpertCompensationCalculatorHistoryRepository;
import com.barraiser.common.graphql.input.ExpertCompensationCalculatorInput;
import com.barraiser.onboarding.graphql.DataFetcherType;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.common.graphql.types.ExpertCompensation;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;


@Log4j2
@RequiredArgsConstructor
@Component
public class GetExpertCompensation implements NamedDataFetcher {

    @Override
    public String name() {
        return "getExpertCompensation";
    }

    @Override
    public String type() {
        return DataFetcherType.QUERY.getValue();
    }

    private final GraphQLUtil graphQLUtil;
    private final ExpertCompensationCalculatorHistoryRepository expertCompensationCalculatorHistoryRepository;

    @Override
    public Object get(DataFetchingEnvironment environment) throws Exception {
        final ExpertCompensationCalculatorInput input = this.graphQLUtil.getInput(environment, ExpertCompensationCalculatorInput.class);

        final ExpertCompensation expertCompensation = this.calculateCompensation(input.getHourPerWeek(), input.getSalary());
        this.saveCompensation(input, expertCompensation);
        return expertCompensation;
    }

    public ExpertCompensation calculateCompensation(final Double hourPerWeek, final Double salary) {
        double minCompensation = Math.ceil((((salary * 100000) / 2060) * hourPerWeek * 52) / 50000) * 50000;
        double maxCompensation = (Math.ceil((minCompensation * 2.5) / 50000) * 5) / 10;
        minCompensation /= 100000;
        return ExpertCompensation.builder()
            .minimumSalary(minCompensation)
            .maximumSalary(maxCompensation)
            .build();
    }

    public void saveCompensation(final ExpertCompensationCalculatorInput input, final ExpertCompensation expertCompensation) {
        this.expertCompensationCalculatorHistoryRepository.save(ExpertCompensationCalculatorHistoryDAO.builder()
            .hourPerWeek(input.getHourPerWeek())
            .salary(input.getSalary())
            .minCompensation(expertCompensation.getMinimumSalary())
            .maxCompensation(expertCompensation.getMaximumSalary())
            .userIdentity(input.getUserIdentity())
            .build());
    }
}
