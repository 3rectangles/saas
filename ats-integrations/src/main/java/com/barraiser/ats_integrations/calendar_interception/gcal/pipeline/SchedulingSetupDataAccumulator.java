/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.calendar_interception.gcal.pipeline;

import com.barraiser.ats_integrations.calendar_interception.*;
import com.barraiser.ats_integrations.calendar_interception.dto.AtsInterview;
import com.barraiser.ats_integrations.calendar_interception.dto.CandidateDetails;
import com.barraiser.ats_integrations.calendar_interception.dto.SchedulingData;
import com.barraiser.ats_integrations.common.client.JobRoleInterviewStructureMappingFeignClient;
import com.barraiser.ats_integrations.common.client.JobRoleManagementFeignClient;
import com.barraiser.ats_integrations.config.ATSPartnerConfigurationManager;
import com.barraiser.ats_integrations.dal.*;
import com.barraiser.ats_integrations.errorhandling.ATSAnomalyException;
import com.barraiser.common.ats_integrations.ATSProvider;
import com.barraiser.commons.dto.ats.enums.ATSAggregator;
import com.barraiser.commons.eventing.schema.commons.calendar.BRCalendarEvent;
import com.barraiser.commons.eventing.schema.commons.calendar.ConferenceEntryChannel;
import com.barraiser.commons.eventing.schema.commons.calendar.ConferencingSolutionConfig;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Log4j2
@Qualifier("Google")
@AllArgsConstructor
@Component
public class SchedulingSetupDataAccumulator implements SchedulingProcessing {

	private final ATSToBREvaluationRepository atsToBREvaluationRepository;
	private final ATSToBRInterviewStructureMappingRepository atsToBRInterviewStructureMappingRepository;
	private final ATSPartnerConfigurationManager atsPartnerConfigurationManager;
	private final ATSCandidateDetailsExtractor ATSCandidateDetailsExtractor;
	private final ATSInterviewDetailsExtractor interviewDetailsExtractor;
	private final JobRoleInterviewStructureMappingFeignClient jobRoleInterviewStructureMappingFeignClient;
	private final ATSJobPostingToBRJobRoleRepository atsJobPostingToBRJobRoleRepository;
	private final JobRoleManagementFeignClient jobRoleManagementFeignClient;
	private final CalendarInterceptionHelper calendarInterceptionHelper;

	private final String ZOOM_MEETING_URL_SUFFIX = "zoom.us";
	private final String ZOOM_MEET_REGEX = "^http.*zoom.*$";
	private final RegexMatchingHelper regexMatchingHelper;
	private final SaasInterviewerManagementHelper saasInterviewerManagementHelper;

	private final static String EVENT_TYPE_SCHEDULING = "SCHEDULING";

	@Override
	public void process(SchedulingData data) throws IOException, ATSAnomalyException {
		final String partnerId = data.getPartnerId();
		this.setInterviewDetails(data);

		final String brEvaluationId = this.getBrEvaluationId(data.getAtsEvaluationId());

		data.setInterviewDuration(this.getInterviewDuration(data.getBrCalendarEvent()));
		data.setIsCalendarInterceptionEnabled(
				this.atsPartnerConfigurationManager.isGoogleCalendarInterceptionEnabled(partnerId));
		data.setInterviewerEmailId(
				this.saasInterviewerManagementHelper.getInterviewerEmail(partnerId, data.getBrCalendarEvent()));
		data.setBrEvaluationId(brEvaluationId);
		data.setPocEmails(this.calendarInterceptionHelper.getPocEmails(data.getBrCalendarEvent()));
		data.setInterviewStart(this.getInterviewStartDate(data.getBrCalendarEvent()));
		data.setInterviewEnd(this.getInterviewEndDate(data.getBrCalendarEvent()));
		data.setOriginalInviteBody(data.getBrCalendarEvent().getDescription());
		data.setOriginalInviteEventId(data.getBrCalendarEvent().getProviderEventId());
		data.setAtsMeetingLink(this.getAtsMeetingLink(data));
		data.setBrInterviewStructureId(
				this.getBrInterviewStructure(data.getPartnerId(), data.getAtsInterviewStructureId(),
						data.getAtsJobRoleId()));
		data.setBrJobRoleId(this.getBrJobRole(data.getAtsJobRoleId(), data.getBrInterviewStructureId()));
		data.setCandidateDetails(
				this.getCandidateDetails(data.getAtsEvaluationId(), data.getAtsProvider(), data
						.getAtsAggregator(), data.getPartnerId()));

		if (data.getBrJobRoleId() == null) {
			data.setIsCalendarInterceptionEnabled(false);
		}

	}

	private String getBrJobRole(final String atsJobRoleId, final String brInterviewStructureId) {

		String brJobRoleId;
		if (atsJobRoleId != null) {
			brJobRoleId = this.atsJobPostingToBRJobRoleRepository.findByAtsJobPostingId(atsJobRoleId).get()
					.getBrJobRoleId();
		} else {
			brJobRoleId = this.jobRoleManagementFeignClient.getJobRole(brInterviewStructureId).getBody().getId();
		}
		if (this.jobRoleManagementFeignClient.isJobRoleInterceptionEnabled(brJobRoleId)) {
			return brJobRoleId;
		}

		log.info(String.format("Exiting interception as job role : %s is not intelligence enabled", brJobRoleId));

		return null;
	}

	private AtsInterview fetchAtsInterview(final String atsInterviewId, final ATSProvider atsProvider,
			final ATSAggregator atsAggregator,
			final String partner) {
		return this.interviewDetailsExtractor.extractAtsInterview(atsInterviewId, atsProvider, atsAggregator, partner);
	}

	private String getAtsMeetingLink(final SchedulingData data) throws ATSAnomalyException {

		if (data.getBrCalendarEvent().getConferencingSolutionConfig() != null) {
			for (final ConferencingSolutionConfig.ConferenceEntryChannelConfig conferenceEntryChannelConfig : data
					.getBrCalendarEvent()
					.getConferencingSolutionConfig().getConferenceEntryChannelConfig()) {
				if (ConferenceEntryChannel.VIDEO.equals(conferenceEntryChannelConfig.getEntryPointType())) {
					return conferenceEntryChannelConfig.getJoiningLink();
				}
			}
		} else if (data.getInviteVariableValueMapping().get("ATS_MEETING_LINK") != null) {
			return data.getInviteVariableValueMapping().get("ATS_MEETING_LINK");
		} else if (data.getBrCalendarEvent().getLocation().contains(ZOOM_MEETING_URL_SUFFIX)) {
			List<String> matchedRegex = regexMatchingHelper.getMatchedValuesForRegex(
					data.getBrCalendarEvent().getLocation(),
					ZOOM_MEET_REGEX);
			if (!matchedRegex.isEmpty()) {
				return matchedRegex.get(0);
			}
		}
		log.warn("Conferencing Solution Config is not present");
		throw new ATSAnomalyException("", "", 1001);
	}

	private String getBrInterviewStructure(final String partnerId, final String atsInterviewStructureId,
			final String atsJobRoleId) {

		if (atsJobRoleId != null) { // TODO: Ideally job role should be known in all cases. Currently we are only
			// doing this for lever so we have to fix this.

			ATSJobPostingToBRJobRoleDAO atsJobPostingToBRJobRoleDAO = this.atsJobPostingToBRJobRoleRepository
					.findByAtsJobPostingId(atsJobRoleId).get();

			return this.jobRoleInterviewStructureMappingFeignClient
					.getBRInterviewStructureId(partnerId, atsJobPostingToBRJobRoleDAO.getBrJobRoleId(),
							this.getBrInterviewStructureIds(atsInterviewStructureId));
		}

		return this.atsToBRInterviewStructureMappingRepository
				.findByAtsInterviewStructureId(atsInterviewStructureId).get()
				.getBrInterviewStructureId();
	}

	public Long getInterviewStartDate(final BRCalendarEvent event) {
		return event.getStart().toEpochSecond();
	}

	public Long getInterviewEndDate(final BRCalendarEvent event) {
		return event.getEnd().toEpochSecond();
	}

	private String getBrEvaluationId(final String atsEvaluationId) {
		return this.atsToBREvaluationRepository
				.findByAtsEvaluationId(atsEvaluationId)
				.orElse(ATSToBREvaluationDAO.builder().brEvaluationId(UUID.randomUUID().toString()).build())
				.getBrEvaluationId();
	}

	@SneakyThrows
	private CandidateDetails getCandidateDetails(final String evaluationId, final ATSProvider atsProvider,
			final ATSAggregator atsAggregator,
			final String partnerId) {

		try {
			return this.ATSCandidateDetailsExtractor.extractCandidateDetails(evaluationId, atsProvider, atsAggregator,
					partnerId);
		} catch (Exception e) {
			log.warn("candidate details fetch failed for ATS evaluation " + evaluationId, e, e);

			return CandidateDetails.builder().build();
		}
	}

	private void setInterviewDetails(final SchedulingData data) {
		final String partnerId = data.getPartnerId();
		final ATSProvider atsProvider = data.getAtsProvider();
		final ATSAggregator atsAggregator = data.getAtsAggregator();

		String atsInterviewStructureId = data.getInviteVariableValueMapping().get("ATS_INTERVIEW_STRUCTURE_ID");
		String atsInterviewFeedbackLink = data.getInviteVariableValueMapping().get("ATS_INTERVIEW_FEEDBACK_LINK");
		String atsEvaluationId = data.getInviteVariableValueMapping().get("ATS_EVALUATION_ID");
		String atsInterviewId = data.getInviteVariableValueMapping().get("ATS_INTERVIEW_ID");

		if (atsInterviewId == null) {
			if (atsEvaluationId == null || atsInterviewStructureId == null) {
				throw new IllegalArgumentException(
						String.format(
								"ATS interception failed for partner : %s and ats : %s for event : %s due to missing data",
								partnerId, atsProvider.getValue(), EVENT_TYPE_SCHEDULING));
			}
		} else if (atsInterviewStructureId == null || atsEvaluationId == null) {
			final AtsInterview atsInterview = this.fetchAtsInterview(atsInterviewId, atsProvider, atsAggregator,
					partnerId);

			atsEvaluationId = atsInterview.getEvaluationId();

			if (atsInterview.getInterviewStructureId() != null)
				atsInterviewStructureId = atsInterview.getInterviewStructureId();
			if (atsInterview.getJobRoleId() != null)
				data.setAtsJobRoleId(atsInterview.getJobRoleId());
			if (atsInterview.getRemoteData() != null)
				data.setRemoteData(atsInterview.getRemoteData());

			log.info("ATS Interception fetched ATSEvaluationId: " + atsEvaluationId);
		}

		data.setAtsEvaluationId(atsEvaluationId);
		data.setAtsInterviewStructureId(atsInterviewStructureId);
		data.setAtsInterviewId(atsInterviewId);
		data.setAtsInterviewFeedbackLink(atsInterviewFeedbackLink);

	}

	private Double getInterviewDuration(BRCalendarEvent event) {
		return (double) Duration.between(event.getStart(), event.getEnd()).toMinutes();
	}

	private List<String> getBrInterviewStructureIds(final String atsInterviewStructureId) {
		List<ATSToBRInterviewStructureMappingDAO> atsToBRInterviewStructureMappingDAOList = this.atsToBRInterviewStructureMappingRepository
				.findAllByAtsInterviewStructureId(atsInterviewStructureId);

		return atsToBRInterviewStructureMappingDAOList.stream()
				.map(ATSToBRInterviewStructureMappingDAO::getBrInterviewStructureId)
				.collect(Collectors.toList());
	}
}
