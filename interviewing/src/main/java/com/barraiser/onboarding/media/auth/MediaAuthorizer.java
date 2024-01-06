/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.media.auth;

import com.barraiser.media_management.dal.MediaDAO;
import com.barraiser.media_management.repository.MediaRepository;
import com.barraiser.onboarding.auth.AuthorizationException;
import com.barraiser.onboarding.auth.ResourceAuthorizer;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.partner.EvaluationManager;
import com.barraiser.onboarding.partner.auth.PartnerPortalAuthorizer;
import com.barraiser.onboarding.user.PartnerEmployeeWhiteLister;
import com.barraiser.commons.auth.UserRole;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@AllArgsConstructor
@Log4j2
public class MediaAuthorizer implements ResourceAuthorizer {

	public final static String MEDIA_CONTEXT_INTERVIEW_RECORDING = "INTERVIEW_RECORDING";
	public final static String RESOURCE_TYPE = "media";
	private final PartnerPortalAuthorizer partnerPortalAuthorizer;
	private final PartnerEmployeeWhiteLister partnerEmployeeWhiteLister;
	private final MediaRepository mediaRepository;
	private final InterViewRepository interViewRepository;
	private final EvaluationManager evaluationManager;

	@Override
	public String type() {
		return RESOURCE_TYPE;
	}

	@Override
	public void can(final AuthenticatedUser user, final String action, final Object resource)
			throws AuthorizationException {
		final String mediaId = this.getMediaIdFromResourceObject(resource);

		switch (action) {
			case "READ":
				this.canRead(user, mediaId);
				break;
			default:
				throw new IllegalArgumentException(
						"no valid action found " + action + " on resource type " + RESOURCE_TYPE);
		}
	}

	private String getMediaIdFromResourceObject(final Object resource) {
		return ((Map<String, String>) resource).get("resourceId");
	}

	public boolean isSuperUser(final AuthenticatedUser user) {
		return user.getRoles().contains(UserRole.OPS) || user.getRoles().contains(UserRole.ADMIN)
				|| user.getRoles().contains(UserRole.QC);
	}

	private void canRead(final AuthenticatedUser user, final String mediaId)
			throws AuthorizationException {

		if (this.isSuperUser(user)) {
			return;
		}

		final MediaDAO mediaDAO = this.mediaRepository.findById(mediaId).get();
		final String context = mediaDAO.getContext();

		if (MEDIA_CONTEXT_INTERVIEW_RECORDING.equalsIgnoreCase(context)) {
			this.canReadInterviewRecordingMedia(user, mediaDAO);
		} else {
			throw new AuthorizationException();
		}
	}

	private void canReadInterviewRecordingMedia(final AuthenticatedUser user, final MediaDAO media)
			throws AuthorizationException {
		final String evaluationId = this.interViewRepository.findById(media.getEntityId()).get().getEvaluationId();
		final String partnerCompanyId = this.evaluationManager.getPartnerCompanyForEvaluation(evaluationId);

		if (this.evaluationManager.isDemoCompany(partnerCompanyId)) {
			return;
		}
		if (!this.partnerPortalAuthorizer.isPartnerRep(user, partnerCompanyId)
				&& !partnerEmployeeWhiteLister.isUserWhiteListedForPartner(user.getEmail(), partnerCompanyId)) {
			throw new AuthorizationException();
		}
	}

}
