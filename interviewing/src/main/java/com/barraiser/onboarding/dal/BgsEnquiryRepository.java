package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BgsEnquiryRepository extends JpaRepository<BgsEnquiryDAO, String> {
}
