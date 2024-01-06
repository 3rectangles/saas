/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.ats_integrations.sync;

import com.barraiser.ats_integrations.dto.ATSPartnerRepMappingsDTO;
import com.barraiser.ats_integrations.dto.ATSUserRoleMappingsDTO;
import com.barraiser.ats_integrations.dto.UpdatePartnerRepMappingsDTO;
import com.barraiser.common.graphql.input.PartnerAccessInput;
import com.barraiser.commons.dto.ats.enums.ATSProvider;
import com.barraiser.commons.dto.jobRoleManagement.ATSPartnerRepInfo;
import com.barraiser.onboarding.ats_integrations.ATSServiceClient;
import com.barraiser.onboarding.dal.PartnerRepsDAO;
import com.barraiser.onboarding.errorhandling.exception.IllegalOperationException;
import com.barraiser.onboarding.partner.PartnerRepUpdationService;
import com.barraiser.onboarding.partner.PartnerRepresentativeAdditionService;
import com.barraiser.onboarding.user.UserInformationManagementHelper;
import com.barraiser.onboarding.user.dto.UserDetailsDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.barraiser.common.constants.Constants.DEFAULT_PARTNER_ROLE;

/**
 * This class is responsible for syncing
 */

/**
 * Cases :
 * <p>
 * Old ats id , but email is new : We are not solving email updation case now.
 * (Not sure how email updations work in ats)
 * <p>
 * old ats id , old email : normal updation of all field
 * <p>
 * new ats id , new email : standard happy case , done
 * <p>
 * new ats id , old email (ie email exists for a user) : This will not be
 * allowed on the ats itself .
 * So not thinking about it. Means If there is a user with primary email
 * ‘jibran@barraiser.com’ ,
 * if we try to change the primary email id of another ats user to
 * ‘jibran@barraiser.com’ ats will
 * not allow (atleast for greenhouse) => Again not solving now.
 */
@Log4j2
@RequiredArgsConstructor
@Component
public class ATSPartnerRepInfoManager {

	private final PartnerRepresentativeAdditionService partnerRepresentativeAdditionService;
	private final PartnerRepUpdationService partnerRepUpdationService;
	private final UserInformationManagementHelper userInformationManagementHelper;
	private final ATSServiceClient atsServiceClient;

	private static final String PARTNER_REP_INFO_MANIPULATION_ACTOR = "ATS_INTEGRATION";

	/**
	 * TODO : Too many params can convert to POJO.
	 */
	@Transactional
	public Boolean process(final String partnerId,
			final ATSProvider atsProvider,
			final ATSPartnerRepInfo atsPartnerRepInfo,
			final String creationSource,
			final String creationSourceMeta)
			throws IOException, IllegalOperationException {

		Map<String, String> atsToBRPartnerRepIdMapping = this.atsServiceClient.getPartnerRepMappings(partnerId)
				.getBody().getPartnerRepMappings()
				.stream()
				.collect(Collectors.toMap(ATSPartnerRepMappingsDTO.PartnerRepMapping::getAtsPartnerRepId,
						ATSPartnerRepMappingsDTO.PartnerRepMapping::getBrPartnerRepId));

		Map<String, String> atsToBRUserRoleIdMapping = this.atsServiceClient.getATSUserRoleMappings(partnerId).getBody()
				.getUserRoleMappings()
				.stream()
				.collect(Collectors.toMap(ATSUserRoleMappingsDTO.UserRoleMapping::getAtsRoleId,
						ATSUserRoleMappingsDTO.UserRoleMapping::getBrRoleId));

		final List<String> brUserRoleIds = this.getBRRoleIdsForATSRoleIds(atsPartnerRepInfo.getRoleIds(),
				atsToBRUserRoleIdMapping);

		final ATSPartnerRepInfo partnerRepInfoWithPartnerId = atsPartnerRepInfo.toBuilder().partnerId(partnerId)
				.build();

		// Update
		if (atsToBRPartnerRepIdMapping.containsKey(atsPartnerRepInfo.getAtsPartnerRepId())) {

			final String brPartnerRepId = atsToBRPartnerRepIdMapping.get(atsPartnerRepInfo.getAtsPartnerRepId());

			this.updatePartnerRep(brPartnerRepId, brUserRoleIds, partnerRepInfoWithPartnerId, creationSource,
					creationSourceMeta);

		} else {// Create
			final String brPartnerRepId = this
					.makePartnerRep(brUserRoleIds, partnerRepInfoWithPartnerId, creationSource, creationSourceMeta)
					.getPartnerRepId();
			this.updatePartnerRepATSMapping(partnerId, atsProvider, brPartnerRepId,
					atsPartnerRepInfo.getAtsPartnerRepId());
		}

		return Boolean.TRUE;
	}

	private void updatePartnerRepATSMapping(final String partnerId, final ATSProvider atsProvider,
			final String brPartnerRepId, final String atsPartnerRepId) {
		this.atsServiceClient.updatePartnerRepMappings(UpdatePartnerRepMappingsDTO.builder()
				.atsProvider(atsProvider)
				.partnerId(partnerId)
				.partnerRepMappings(
						List.of(
								UpdatePartnerRepMappingsDTO.PartnerRepMapping.builder()
										.brPartnerRepId(brPartnerRepId)
										.atsPartnerRepId(atsPartnerRepId)
										.build()))
				.build());
	}

	/**
	 * NOTE : We are not handling the case where the same email id is getting used
	 * for two ats users
	 */
	private PartnerRepsDAO makePartnerRep(final List<String> brUserRoleIds, final ATSPartnerRepInfo atsPartnerRep,
			final String creationSource, final String creationSourceMeta)
			throws IOException {

		final String userId = this.userInformationManagementHelper
				.addOrUpdateUser(this.toUserDetailsDTO(atsPartnerRep));

		return this.partnerRepresentativeAdditionService.addPartnerRep(
				userId,
				PartnerAccessInput.builder()
						.email(atsPartnerRep.getEmail())
						.partnerId(atsPartnerRep.getPartnerId())
						.firstName(atsPartnerRep.getFirstName())
						.lastName(atsPartnerRep.getLastName())
						.phone(atsPartnerRep.getPhone())
						.roles(brUserRoleIds)
						.build(),
				PARTNER_REP_INFO_MANIPULATION_ACTOR, creationSource, creationSourceMeta);
	}

	/**
	 * NOTE : We are not supporting email id change for the same ats user id right
	 * now. Adds complications
	 * like detaching the user from partner reps table and updating that entry as
	 * well, etc.
	 * Needs custom handling , will see on case basis.
	 */
	private void updatePartnerRep(final String existingBrPartnerRepId, final List<String> brUserRoleIds,
			final ATSPartnerRepInfo atsPartnerRep, final String creationSource, final String creationSourceMeta)
			throws IOException {

		this.userInformationManagementHelper.updateUserDetailsFromDTO(existingBrPartnerRepId, UserDetailsDTO.builder()
				.partnerId(atsPartnerRep.getPartnerId())
				.firstName(atsPartnerRep.getFirstName())
				.lastName(atsPartnerRep.getLastName())
				.email(atsPartnerRep.getEmail())
				.phone(atsPartnerRep.getPhone())
				.build());

		this.partnerRepUpdationService.updatePartnerRep(
				existingBrPartnerRepId,
				PartnerAccessInput.builder()
						.partnerId(atsPartnerRep.getPartnerId())
						.email(atsPartnerRep.getEmail())
						.firstName(atsPartnerRep.getFirstName())
						.lastName(atsPartnerRep.getLastName())
						.phone(atsPartnerRep.getPhone())
						.roles(brUserRoleIds)
						.build(),
				PARTNER_REP_INFO_MANIPULATION_ACTOR, creationSource, creationSourceMeta);
	}

	private List<String> getBRRoleIdsForATSRoleIds(final List<String> atsRoleIds,
			final Map<String, String> atsToBRUserRoleIdMapping) {
		return atsRoleIds.stream()
				.map(e -> atsToBRUserRoleIdMapping.containsKey(e) ? atsToBRUserRoleIdMapping.get(e)
						: DEFAULT_PARTNER_ROLE)
				.collect(Collectors.toList());
	}

	private UserDetailsDTO toUserDetailsDTO(final ATSPartnerRepInfo atsPartnerRepInfo) {
		return UserDetailsDTO.builder()
				.email(atsPartnerRepInfo.getEmail())
				.phone(atsPartnerRepInfo.getPhone())
				.firstName(atsPartnerRepInfo.getFirstName())
				.lastName(atsPartnerRepInfo.getLastName())
				.partnerId(atsPartnerRepInfo.getPartnerId())
				.build();
	}
}
