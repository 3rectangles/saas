package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewRoundTypeConfigurationRepository extends JpaRepository<InterviewRoundTypeConfigurationDAO, String> {
    Optional<InterviewRoundTypeConfigurationDAO> findByRoundType(final String roundType);
    List<InterviewRoundTypeConfigurationDAO> findAllByRoundTypeIn(final List<String> roundTypes);
    Optional<InterviewRoundTypeConfigurationDAO> findByRoundTypeAndCompanyId(final String roundType,final String companyId);
}


