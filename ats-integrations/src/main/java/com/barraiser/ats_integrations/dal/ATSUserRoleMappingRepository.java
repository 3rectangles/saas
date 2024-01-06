/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.dal;

import com.barraiser.commons.dto.ats.enums.ATSProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ATSUserRoleMappingRepository extends JpaRepository<ATSUserRoleMappingDAO, String> {

	List<ATSUserRoleMappingDAO> findByAtsProviderAndPartnerId(final String atsProvider, final String partnerId);

	List<ATSUserRoleMappingDAO> findByPartnerId(final String partnerId);

	Optional<ATSUserRoleMappingDAO> findByAtsProviderAndPartnerIdAndAtsUserRoleId(final String atsProvider,
			final String partnerId, final String atsUserRoleId);

	void deleteByIdIn(List<String> atsUserRoleMappingIds);
}
