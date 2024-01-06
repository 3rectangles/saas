package com.barraiser.onboarding.user;

import com.barraiser.common.graphql.types.Partner;
import com.barraiser.common.graphql.types.PartnerDetails;
import com.barraiser.onboarding.dal.CompanyDAO;
import com.barraiser.onboarding.dal.CompanyRepository;
import com.barraiser.onboarding.dal.UserDetailsDAO;
import com.barraiser.onboarding.graphql.MultiParentTypeDataFetcher;

import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLObjectType;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Component
public class CompanyDataFetcher implements MultiParentTypeDataFetcher {
    private final CompanyRepository companyRepository;

    @Override
    public List<List<String>> typeNameMap() {
        return List.of(
                List.of("Interviewer", "currentCompany"),
                List.of("Partner", "companyDetails"),
                List.of("PartnerDetail", "company"));
    }

    @Override
    public Object get(final DataFetchingEnvironment environment) throws Exception {
        final GraphQLObjectType parentType = (GraphQLObjectType) environment.getParentType();

        if (parentType.getName().equals("Interviewer")) {
            final UserDetailsDAO userDetailsDAO = environment.getLocalContext();
            if (userDetailsDAO == null) {
                return DataFetcherResult.newResult().data(CompanyDAO.builder().build()).build();
            }
            final CompanyDAO companyDAO;
            if (userDetailsDAO.getCurrentCompanyId() != null) {
                companyDAO =
                        this.companyRepository
                                .findById(userDetailsDAO.getCurrentCompanyId())
                                .orElse(
                                        CompanyDAO.builder()
                                                .name(userDetailsDAO.getCurrentCompanyName())
                                                .build());
            } else {
                companyDAO =
                        CompanyDAO.builder().name(userDetailsDAO.getCurrentCompanyName()).build();
            }
            return DataFetcherResult.newResult().data(companyDAO).build();
        } else if (parentType.getName().equals("Partner")) {
            final Partner partner = environment.getSource();
            final CompanyDAO companyDAO = getCompanyById(partner.getCompanyId());
            return DataFetcherResult.newResult().data(companyDAO).build();
        } else if (parentType.getName().equals("PartnerDetail")) {
            final PartnerDetails partnerDetails = environment.getSource();
            final CompanyDAO companyDAO = getCompanyById(partnerDetails.getCompanyId());
            return DataFetcherResult.newResult().data(companyDAO).build();
        } else {
            throw new IllegalArgumentException("Bad parent type : " + parentType.getName());
        }
    }

    private CompanyDAO getCompanyById(String companyId) {
        final Optional<CompanyDAO> companyDAO = this.companyRepository.findById(companyId);
        return (companyDAO.orElse(null));
    }
}
