/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.authorization.repository;

import com.barraiser.authorization.dal.PermissionDAO;
import com.barraiser.commons.auth.Action;
import com.barraiser.commons.auth.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<PermissionDAO, String> {

	Optional<PermissionDAO> findByActionAndResource(Action action, Resource resource);

	List<PermissionDAO> findByActionInAndResource(List<Action> action, Resource resource);
}
