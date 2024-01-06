package com.barraiser.onboarding.audit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntityAuditHistoryRepository extends JpaRepository<EntityAuditHistoryDAO, String> {
}
