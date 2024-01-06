package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TargetJobAttributesRepository extends JpaRepository<TargetJobAttributesDAO, String> {
    Optional<TargetJobAttributesDAO> findByUserId(@Param("userId") String userId);
}
