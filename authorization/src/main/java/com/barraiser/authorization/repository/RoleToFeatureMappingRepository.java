/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.authorization.repository;

import com.barraiser.authorization.dal.RoleToFeatureMappingDAO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface RoleToFeatureMappingRepository extends JpaRepository<RoleToFeatureMappingDAO, String> {
	List<RoleToFeatureMappingDAO> findByRoleIdIn(Set<String> roleIds);
}
