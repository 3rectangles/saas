/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.authorization.repository;

import com.barraiser.authorization.dal.RoleDAO;
import com.barraiser.commons.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<RoleDAO, String> {
	List<RoleDAO> findByType(RoleType type);

	List<RoleDAO> findByPartnerId(String partnerId);
}
