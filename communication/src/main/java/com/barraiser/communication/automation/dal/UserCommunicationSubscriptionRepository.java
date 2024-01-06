package com.barraiser.communication.automation.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCommunicationSubscriptionRepository extends JpaRepository<UserCommunicationSubscriptionDAO, String> {
    Optional<UserCommunicationSubscriptionDAO> findByUserIdAndEventType(String userId, String eventType);
}
