package com.barraiser.onboarding.candidate;

import com.barraiser.onboarding.dal.CandidateCompensationCalculatorHistoryDAO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidateCompensationCalculatorHistoryRepository extends JpaRepository<CandidateCompensationCalculatorHistoryDAO,String> {
}
