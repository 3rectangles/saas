/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDetailsRepository extends JpaRepository<UserDetailsDAO, String> {

	List<UserDetailsDAO> findAllByIdIn(List<String> userIds);

	List<UserDetailsDAO> findAllByIsActiveAndIsExpertPartner(boolean isActive, boolean isExpertPartner);

	List<UserDetailsDAO> findByEmailIn(List<String> emails);

	Optional<UserDetailsDAO> findByEmail(String email);

	List<UserDetailsDAO> findByRoleContaining(String role);
}
