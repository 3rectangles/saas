package com.barraiser.communication.automation.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunicationTemplateConfigRepository extends JpaRepository<CommunicationTemplateConfigDAO, String> {
    List<CommunicationTemplateConfigDAO> findAllByEventTypeAndPartnerId(String eventType, String partnerId);
}
