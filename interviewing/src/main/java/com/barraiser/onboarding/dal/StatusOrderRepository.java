package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatusOrderRepository extends JpaRepository<StatusOrderDAO, String> {
    List<StatusOrderDAO> findAllByStatusIdIn(List<String> statusIds);
}
