package com.barraiser.onboarding.user;

import com.barraiser.onboarding.dal.CompanyDAO;
import com.barraiser.onboarding.dal.CompanyRepository;
import com.barraiser.onboarding.dal.UserDetailsDAO;
import com.barraiser.onboarding.graphql.Constants;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class LastCompaniesDataFetcher implements NamedDataFetcher {
    private final CompanyRepository companyRepository;

    @Override
    public String name() {
        return "lastCompanies";
    }

    @Override
    public String type() {
        return Constants.TYPE_INTERVIEWER;
    }

    @Override
    public Object get(final DataFetchingEnvironment environment) throws Exception {
        final UserDetailsDAO userDetailsDAO = environment.getSource();
        final List<CompanyDAO> lastCompanies = userDetailsDAO.getLastCompanies() == null ? new ArrayList<>() :
                userDetailsDAO.getLastCompanies().stream()
                        .map(this.companyRepository::findById)
                        .map(x -> x.orElse(null))
                        .collect(Collectors.toList());
        return DataFetcherResult.newResult()
                .data(lastCompanies)
                .build();
    }
}
