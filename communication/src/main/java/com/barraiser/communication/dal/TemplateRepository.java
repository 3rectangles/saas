package com.barraiser.communication.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemplateRepository extends JpaRepository<TemplateDAO, String> {

    TemplateDAO findAllByEventType(String eventType);
}
