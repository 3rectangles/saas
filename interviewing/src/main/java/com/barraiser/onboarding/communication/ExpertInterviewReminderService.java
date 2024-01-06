/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.communication;

import com.barraiser.onboarding.cms.CMSManager;
import com.barraiser.onboarding.cms.pages.ReminderEmailPage;
import com.barraiser.common.utilities.DateUtils;
import com.barraiser.onboarding.communication.channels.email.EmailEvent;
import com.barraiser.onboarding.communication.channels.email.EmailService;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.InterviewStatus;
import com.barraiser.onboarding.dal.UserDetailsDAO;
import com.barraiser.onboarding.dal.UserDetailsRepository;
import com.barraiser.onboarding.dal.specifications.InterviewSpecifications;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.buttercms.model.Page;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Log4j2
@Component
@AllArgsConstructor
public class ExpertInterviewReminderService implements EmailHandler {

	private final InterViewRepository interViewRepository;
	private final UserDetailsRepository userDetailsRepository;
	private final InterviewSpecifications interviewSpecifications;
	public static final String TEMPLATE = "reminder_mail_to_expert_regarding_interviews";
	public static final String INTERVIEW_DATE_TIME_FORMAT = "dd MMM uuuu, hh:mm a z";
	public static final String INTERVIEW_BARRAISER_EMAIL_ID = "interview@barraiser.com";
	private final DateUtils utilities;
	private final EmailService emailService;
	private final CMSManager cmsManager;
	public static final String SEND_REMINDER_MAIL_TO_EXPERT = "send_reminder_email_to_expert";

	@Override
	public String objective() {
		return SEND_REMINDER_MAIL_TO_EXPERT;
	}

	@Override
	public String subject() {
		return "REMINDER: BarRaiser Interview";
	}

	@Override
	public void process(final EmailEvent emailEvent) {
		final LocalDateTime currentDateTime = LocalDateTime.now();
		final Long startTimeStamp = LocalDate
				.of(currentDateTime.getYear(), currentDateTime.getMonth(), currentDateTime.getDayOfMonth())
				.atTime(0, 0, 0).atZone(ZoneId.of("Asia/Kolkata")).toEpochSecond();

		final Long endTimeStamp = LocalDate
				.of(currentDateTime.getYear(), currentDateTime.getMonth(), currentDateTime.getDayOfMonth())
				.atTime(23, 59, 59).atZone(ZoneId.of("Asia/Kolkata")).toEpochSecond();

		if (emailEvent.getExpertId() != null) {
			final Specification<InterviewDAO> specification = this.interviewSpecifications
					.getExpertsInterviewsSpecification(emailEvent.getExpertId(), startTimeStamp, endTimeStamp, null,
							List.of(InterviewStatus.CANCELLATION_DONE.getValue()));
			this.sendMailToExpert(this.getAllInterviewsOfExpertsForTheDay(specification));

		} else {
			final Specification<InterviewDAO> specification = this.interviewSpecifications
					.getExpertsInterviewsSpecification(null, startTimeStamp, endTimeStamp, null,
							List.of(InterviewStatus.CANCELLATION_DONE.getValue()));
			this.sendMailToExpert(this.getAllInterviewsOfExpertsForTheDay(specification));

		}
	}

	public Map<String, List<InterviewDAO>> getAllInterviewsOfExpertsForTheDay(
			final Specification<InterviewDAO> specification) {

		final List<InterviewDAO> interviewList = this.interViewRepository.findAll(specification);

		interviewList.sort(Comparator.comparing(InterviewDAO::getStartDate));

		final Map<String, List<InterviewDAO>> expertInterviewMap = new HashMap<>();

		for (final InterviewDAO interviewDAO : interviewList) {
			final List<InterviewDAO> expertInterviews = expertInterviewMap.getOrDefault(interviewDAO.getInterviewerId(),
					new ArrayList<>());
			expertInterviews.add(interviewDAO);
			expertInterviewMap.put(interviewDAO.getInterviewerId(), expertInterviews);
		}

		return expertInterviewMap;
	}

	private void sendMailToExpert(final Map<String, List<InterviewDAO>> expertInterviewMap) {
		expertInterviewMap.forEach((x, y) -> {
			final UserDetailsDAO expert = this.userDetailsRepository.findById(x).orElse(null);
			final Map<String, Object> interviewData = this.constructMailData(expert, y);

			final List<String> toEmail = new ArrayList<>();
			toEmail.add(expert.getEmail());

			final List<String> ccEmail = new ArrayList<>();
			ccEmail.add(INTERVIEW_BARRAISER_EMAIL_ID);

			final String subject = interviewData.get("subject").toString();

			try {
				this.emailService.sendEmailForObjectData(INTERVIEW_BARRAISER_EMAIL_ID, subject, TEMPLATE, toEmail,
						ccEmail, interviewData, null);
			} catch (final IOException e) {
				log.error(String.format("Error in sending reminder mail to expert: %s", x));
				throw new RuntimeException(e);
			}
		});
	}

	private Map<String, Object> constructMailData(final UserDetailsDAO expert, final List<InterviewDAO> interviewList) {

		final Map<String, Object> data = this.getEmailContentFromCMS();
		final List<String> interviewsDetails = new ArrayList<>();
		data.put("expertName", new String[] { expert.getFirstName() });

		for (final InterviewDAO interview : interviewList) {
			final String startTime = this.utilities.getFormattedDateString(interview.getStartDate(),
					expert.getTimezone(), INTERVIEW_DATE_TIME_FORMAT);
			interviewsDetails.add(startTime);
		}

		data.put("interviewTimings", interviewsDetails.toArray(String[]::new));
		return data;
	}

	public Map<String, Object> getEmailContentFromCMS() {
		final Page<ReminderEmailPage> reminderEmailPage = this.cmsManager.getPage("reminder-email",
				ReminderEmailPage.class, true);
		final HashMap<String, Object> emailContentMap = new HashMap<>();
		emailContentMap.put("body", reminderEmailPage.getFields().getBody());
		emailContentMap.put("subject", reminderEmailPage.getFields().getSubject());
		emailContentMap.put("title", reminderEmailPage.getFields().getTitle());
		return emailContentMap;
	}
}
