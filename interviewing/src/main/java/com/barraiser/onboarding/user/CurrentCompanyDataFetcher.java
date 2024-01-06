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

@Component
@AllArgsConstructor
public class CurrentCompanyDataFetcher implements NamedDataFetcher {
    private final CompanyRepository companyRepository;

    @Override
    public String name() {
        return "currentCompany";
    }

    @Override
    public String type() {
        return Constants.TYPE_INTERVIEWER;
    }

    @Override
    public Object get(final DataFetchingEnvironment environment) throws Exception {
        final UserDetailsDAO userDetailsDAO = environment.getSource();

        final CompanyDAO companyDAO;
        if (userDetailsDAO.getCurrentCompanyId() == null) {
            companyDAO = null;
        } else {
            companyDAO = this.companyRepository
                    .findById(userDetailsDAO.getCurrentCompanyId())
                    .orElse(null);
        }
        return DataFetcherResult.newResult()
                .data(companyDAO)
                .build();
    }
}
