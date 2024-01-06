package com.barraiser.communication.automation.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunicationLogRepository extends JpaRepository<CommunicationLogDAO, String> {
}
