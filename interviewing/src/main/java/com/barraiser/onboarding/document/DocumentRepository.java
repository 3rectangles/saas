package com.barraiser.onboarding.document;

import com.barraiser.onboarding.dal.DocumentDAO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * This repository is used to access a table that
 * stores file information with url , name , link etc.
 */
public interface DocumentRepository extends JpaRepository<DocumentDAO, String> {
    Optional<DocumentDAO> findByFileUrl(String fileUrl);
}
