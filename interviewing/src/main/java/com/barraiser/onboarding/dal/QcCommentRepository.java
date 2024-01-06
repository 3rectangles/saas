package com.barraiser.onboarding.dal;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QcCommentRepository extends JpaRepository<QcCommentDAO, String> {

    List<QcCommentDAO> findByFeedbackIdOrderByCreatedOnDesc(String feedbackId);

    List<QcCommentDAO> findByFeedbackIdIn(List<String> feedbackIds);
    
    void deleteByFeedbackIdIn(List<String> feedbackIds);
}
