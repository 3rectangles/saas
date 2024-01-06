package com.barraiser.common.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventToEntityRepository extends JpaRepository<EventToEntityDAO, String> {
    Optional<EventToEntityDAO> findByEventType(String eventType);
}
