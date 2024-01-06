/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.authorization.repository;

import com.barraiser.authorization.dal.UserToRoleMappingDAO;
import com.barraiser.commons.auth.Dimension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface UserToRoleMappingRepository extends JpaRepository<UserToRoleMappingDAO, String> {

	List<UserToRoleMappingDAO> findByUserIdAndAuthorizationDimensionInAndDeletedOnIsNull(String userId,
			List<Dimension> dimensions);

	List<UserToRoleMappingDAO> findByUserIdAndAuthorizationDimensionAndAuthorizationDimensionValueAndDeletedOnIsNull(
			String userId,
			Dimension authorizationDimension, String authorizationDimensionValue);

	List<UserToRoleMappingDAO> findByUserIdAndAuthorizationDimensionAndAuthorizationDimensionValueInAndRoleIdInAndDeletedOnIsNull(
			String userId,
			Dimension authorizationDimension,
			List<String> authorizationDimensionValue,
			List<String> roleIds);

	List<UserToRoleMappingDAO> findByUserIdAndRoleIdInAndDeletedOnIsNull(String userId, List<String> roleIds);

	@Transactional
	void deleteByUserIdAndAuthorizationDimensionAndAuthorizationDimensionValueAndRoleIdIn(String userId,
			Dimension authorizationDimesion, String authorizationDimensionValue, List<String> roleids);

	List<UserToRoleMappingDAO> findByAuthorizationDimensionAndAuthorizationDimensionValueInAndDeletedOnIsNull(
			Dimension authorizationDimension,
			List<String> authorizationDimensionValue);

}
