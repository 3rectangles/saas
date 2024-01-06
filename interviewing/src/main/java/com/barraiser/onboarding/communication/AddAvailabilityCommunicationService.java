/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.communication;

import com.barraiser.common.utilities.DateUtils;
import com.barraiser.onboarding.availability.AvailabilitySlot;
import com.barraiser.onboarding.common.StaticAppConfigValues;
import com.barraiser.onboarding.communication.channels.email.EmailService;
import com.barraiser.onboarding.dal.UserDetailsDAO;
import com.barraiser.onboarding.dal.UserDetailsRepository;
import com.barraiser.onboarding.interview.jira.JiraWorkflowManager;
import com.barraiser.onboarding.interview.jira.dto.JiraCommentDTO;
import com.barraiser.onboarding.scheduling.scheduling.MatchInterviewersDataHelper;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.EligibleInterviewersFetcher;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.InterviewerData;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.MatchInterviewersData;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class AddAvailabilityCommunicationService {
	public static final String JIRA_COMMENT_BODY_FOR_NOTIFYING_CANDIDATE_SLOTS = "Team, Candidate has requested for following slots for interview \n"
			+ " %s Kindly find the availability of the following experts in these slots and"
			+ " schedule the interview : \n"
			+ " %s";
	public static final String JIRA_COMMENT_BODY_FOR_ZERO_ELIGIBLE_EXPERTS = "Team, Candidate has requested for following slots for interview \n %s "
			+ "Please use old scheduling tool for scheduling this interview";

	private final UserDetailsRepository userDetailsRepository;
	private final MatchInterviewersDataHelper matchInterviewersDataHelper;
	private final DateUtils dateUtils;
	private final StaticAppConfigValues staticAppConfigValues;
	private final EmailService emailService;
	private final JiraWorkflowManager jiraWorkflowManager;
	private final EligibleInterviewersFetcher eligibleInterviewersFetcher;

	public void communicateAdditionOfAvailabilityOfExpert(
			final List<AvailabilitySlot> slots, final String userId) throws Exception {
		try {
			final String opsEmailId = this.staticAppConfigValues.getInterviewLifecycleInformationEmail();
			final Map<String, Object> data = this.constructEmailData(userId, slots);
			this.emailService.sendEmailForObjectData(
					opsEmailId,
					data.get("userName") + " has submitted his/her availability",
					"ops_email_for_availability_submission",
					List.of(opsEmailId),
					null,
					data,
					null);
		} catch (final Exception e) {
			throw new Exception(
					"Error sending email to ops during availability submission" + e.getMessage());
		}
	}

	public void communicateChangeOfAvailabilityOfCandidate(
			final String contextId, final List<AvailabilitySlot> slots) {
		this.addCommentOnJira(contextId, slots);
	}

	public List<AvailabilitySlot> getSlots(final List<AvailabilitySlot> inputSlots) {
		final List<AvailabilitySlot> slots = new ArrayList<>();
		final AtomicInteger index = new AtomicInteger();
		inputSlots.forEach(
				x -> {
					final String date = this.dateUtils.getFormattedDateString(
							x.getStartDate(), null, "yyyy-MM-dd");
					final String startDate = this.dateUtils.getFormattedDateString(
							x.getStartDate(), null, "HH:mm a");
					final String endDate = this.dateUtils.getFormattedDateString(x.getEndDate(), null, "HH:mm a");
					slots.add(
							AvailabilitySlot.builder()
									.index(index.incrementAndGet())
									.date(date)
									.formattedStartTime(startDate)
									.formattedEndTime(endDate)
									.build());
				});
		return slots;
	}

	private void addCommentOnJira(final String contextId, List<AvailabilitySlot> slots) {
		final AtomicInteger index = new AtomicInteger(1);
		String dateComments = "";
		slots = this.getSlots(slots);
		for (AvailabilitySlot slot : slots) {
			dateComments += index.getAndIncrement()
					+ ". "
					+ slot.getDate()
					+ " : "
					+ slot.getFormattedStartTime()
					+ " - "
					+ slot.getFormattedEndTime()
					+ " IST\n";
		}
		String comment;
		try {
			final MatchInterviewersData data = this.matchInterviewersDataHelper.prepareDataForInterviewSlots(contextId);
			this.eligibleInterviewersFetcher.populateEligibleInterviewers(data);
			final List<String> interviewers = data.getInterviewers().stream()
					.map(InterviewerData::getId)
					.collect(Collectors.toList());
			if (interviewers.size() != 0) {
				comment = String.format(
						JIRA_COMMENT_BODY_FOR_NOTIFYING_CANDIDATE_SLOTS,
						dateComments,
						this.getUserDetailsCommentBody(interviewers));
			} else {
				comment = String.format(JIRA_COMMENT_BODY_FOR_ZERO_ELIGIBLE_EXPERTS, dateComments);
			}
		} catch (final IllegalArgumentException | IOException e) {
			comment = String.format(JIRA_COMMENT_BODY_FOR_ZERO_ELIGIBLE_EXPERTS, dateComments);
			log.warn(e, e);
		}
		this.jiraWorkflowManager.addCommentInJira(
				contextId, JiraCommentDTO.builder().body(comment).build());
	}

	public String getUserDetailsCommentBody(final List<String> userIds) {
		final List<UserDetailsDAO> userDetailsDAOs = this.userDetailsRepository.findAllByIdIn(userIds);
		String userDetailsComment = "";
		int index = 1;
		for (UserDetailsDAO user : userDetailsDAOs) {
			userDetailsComment += "E"
					+ index++
					+ "("
					+ user.getFirstName()
					+ " "
					+ user.getLastName()
					+ " "
					+ user.getPhone()
					+ " "
					+ user.getEmail()
					+ ") \n";
		}
		return userDetailsComment;
	}

	private Map<String, Object> constructEmailData(
			final String userId, final List<AvailabilitySlot> slots) {

		final Map<String, Object> data = new HashMap<>();
		final Optional<UserDetailsDAO> userDetailsDAO = this.userDetailsRepository.findById(userId);
		final String firstName = userDetailsDAO.get().getFirstName();
		final String lastName = userDetailsDAO.get().getLastName();
		data.put("userId", userId);
		data.put("userName", (firstName + " " + lastName));
		data.put("Date", this.getSlots(slots));
		return data;
	}
}
