package com.barraiser.onboarding.resume;

import com.barraiser.onboarding.dal.ParsedResumeDAO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParsedResumeRepository extends JpaRepository<ParsedResumeDAO, String> {
    ParsedResumeDAO findByDocumentId(String documentId);
}
