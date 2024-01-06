/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.authorization.repository;

import com.barraiser.authorization.dal.RoleToPermissionMappingDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleToPermissionMappingRepository extends JpaRepository<RoleToPermissionMappingDAO, String> {

	List<RoleToPermissionMappingDAO> findByRoleIdIn(List<String> roleIds);

	List<RoleToPermissionMappingDAO> findByPermissionId(String permissionId);

	List<RoleToPermissionMappingDAO> findByPermissionIdIn(List<String> permissionId);
}
