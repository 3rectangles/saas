package com.barraiser.onboarding.interview.certificate;
import com.barraiser.onboarding.dal.DocumentDAO;
import com.barraiser.onboarding.interview.certificate.CertificateDAO;
import org.springframework.data.jpa.repository.JpaRepository;
/**
 * This repository is used to access a table that
 * stores file information with certificateId , evaluationId , imageUrl etc.
 */
public interface CertificateRepository extends JpaRepository<CertificateDAO, String> {
}
