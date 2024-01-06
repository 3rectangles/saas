package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EvaluationAccessFilterRepository extends JpaRepository<EvaluationAccessFilterDAO, String> {
    Optional<EvaluationAccessFilterDAO> findByUserIdAndPartnerId(final String userId, final String partnerId);
}
