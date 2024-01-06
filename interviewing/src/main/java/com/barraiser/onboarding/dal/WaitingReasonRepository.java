package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WaitingReasonRepository extends JpaRepository<WaitingReasonDAO, String> {

    Optional<WaitingReasonDAO> findByIdAndProcessType(String id, String processType);
}
