package com.barraiser.onboarding.expert;

import com.barraiser.onboarding.dal.ExpertCompensationCalculatorHistoryDAO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpertCompensationCalculatorHistoryRepository extends JpaRepository<ExpertCompensationCalculatorHistoryDAO, String> {
}
