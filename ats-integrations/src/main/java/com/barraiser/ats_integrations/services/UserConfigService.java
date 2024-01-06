/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.services;

import com.barraiser.ats_integrations.dal.ATSUserRoleMappingDAO;
import com.barraiser.ats_integrations.dal.ATSUserRoleMappingRepository;
import com.barraiser.ats_integrations.dto.ATSUserRoleMappingsDTO;
import com.barraiser.commons.dto.User.role.UpdateUserRoleMappingInput;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.barraiser.common.constants.Constants.DEFAULT_PARTNER_ROLE;

@Component
@Log4j2
@AllArgsConstructor
public class UserConfigService {

	private ATSUserRoleMappingRepository atsUserRoleMappingRepository;

	@Transactional
	public void updateUserRoleMapping(final UpdateUserRoleMappingInput updateUserRoleMappingData) {

		updateUserRoleMappingData.getUserRoleMappings().stream()
				.forEach(x -> this.updateUserRoleMapping(updateUserRoleMappingData.getAtsProvider(),
						updateUserRoleMappingData.getPartnerId(), x));

	}

	private void updateUserRoleMapping(final String atsProvider, final String partnerId,
			final UpdateUserRoleMappingInput.UserRoleMapping userRoleMapping) {

		final ATSUserRoleMappingDAO atsUserRoleMappingDAO = this.atsUserRoleMappingRepository
				.findByAtsProviderAndPartnerIdAndAtsUserRoleId(atsProvider, partnerId,
						userRoleMapping.getAtsUserRoleId())
				.orElse(ATSUserRoleMappingDAO.builder()
						.id(UUID.randomUUID().toString())
						.atsUserRoleId(userRoleMapping.getAtsUserRoleId())
						.atsProvider(atsProvider)
						.partnerId(partnerId)
						.build());

		final ATSUserRoleMappingDAO updatedATSUserRoleDAO = atsUserRoleMappingDAO.toBuilder()
				.brUserRoleId(userRoleMapping.getBrUserRoleId() != null ? userRoleMapping.getBrUserRoleId()
						: DEFAULT_PARTNER_ROLE)
				.atsUserRoleName(userRoleMapping.getAtsUserRoleName())
				.build();

		this.atsUserRoleMappingRepository.save(updatedATSUserRoleDAO);

	}

	public List<ATSUserRoleMappingsDTO.UserRoleMapping> getUserRoleMappings(final String partnerId) {

		return this.atsUserRoleMappingRepository.findByPartnerId(partnerId)
				.stream()
				.map(x -> ATSUserRoleMappingsDTO.UserRoleMapping.builder()
						.brRoleId(x.getBrUserRoleId())
						.atsRoleId(x.getAtsUserRoleId())
						.atsProvider(x.getAtsProvider())
						.atsRoleName(x.getAtsUserRoleId())
						.partnerId(x.getPartnerId())
						.build())
				.collect(Collectors.toList());
	}

}
