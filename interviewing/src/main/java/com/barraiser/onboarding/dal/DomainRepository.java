package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DomainRepository extends JpaRepository<DomainDAO, String> {
    Optional<DomainDAO> findByNameIgnoreCase(String name);
    List<DomainDAO> findAllByIdIn(List<String> ids);
}
