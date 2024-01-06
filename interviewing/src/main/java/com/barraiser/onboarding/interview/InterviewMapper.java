/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.enums.MeetingPlatform;
import com.barraiser.common.graphql.types.Interview;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interviewing.meeting.InterviewMeetingUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class InterviewMapper {
	private final PartnerCompanyRepository partnerCompanyRepository;
	private final EvaluationRepository evaluationRepository;
	private final InterviewHistoryRepository interviewHistoryRepository;
	private final CancellationReasonRepository cancellationReasonRepository;
	private final InterviewUtil interviewUtil;
	private final InterviewMeetingUtils interviewMeetingUtils;

	public Interview toInterview(final InterviewDAO interviewDAO) {
		return Interview.builder()
				.id(interviewDAO.getId())
				.intervieweeId(interviewDAO.getIntervieweeId())
				.interviewerId(interviewDAO.getInterviewerId())
				.interviewRound(interviewDAO.getInterviewRound())
				.startDate(interviewDAO.getStartDate())
				.videoStartTime(interviewDAO.getVideoStartTime())
				.endDate(interviewDAO.getEndDate())
				.cancellationTime(Long.getLong(interviewDAO.getCancellationTime()))
				.actualEndDate(interviewDAO.getActualEndDate())
				.lastQuestionEnd(interviewDAO.getLastQuestionEnd())
				.status(interviewDAO.getStatus())
				.youtubeLink(interviewDAO.getYoutubeLink())
				.interviewStructureId(interviewDAO.getInterviewStructureId())
				.opsRep(interviewDAO.getOpsRep())
				.zoomLink(interviewDAO.getZoomLink())
				.feedbackStatus(interviewDAO.getFeedbackStatus())
				.questionTaggingStatus(interviewDAO.getQuestionTaggingStatus())
				.videoLink(interviewDAO.getVideoLink())
				.rating(interviewDAO.getRating())
				.remarks(interviewDAO.getRemarks())
				.evaluationId(interviewDAO.getEvaluationId())
				.taggingAgent(interviewDAO.getTaggingAgent())
				.cancellationReasonId(interviewDAO.getCancellationReasonId())
				.submittedCodeLink(interviewDAO.getSubmittedCodeLink())
				.createdOn(interviewDAO.getCreatedOn())
				.isRescheduled(interviewDAO.getIsRescheduled())
				.audioLink(interviewDAO.getAudioLink())
				.rescheduleCount(interviewDAO.getRescheduleCount())
				.partnerId(interviewDAO.getPartnerId())
				.message(this.getMessage(interviewDAO))
				.roundNumber(this.interviewUtil.getRoundNumberOfInterview(interviewDAO))
				.updatedOn(interviewDAO.getUpdatedOn().getEpochSecond())
				.meetingLink(interviewDAO.getMeetingLink())
				.areHighlightsComplete(interviewDAO.getAreHighlightsComplete())
				.meetingPlatform(
						MeetingPlatform.valueOf(
								this.interviewMeetingUtils.getMeetingPlatformFromURL(
										interviewDAO.getMeetingLink()).getValue()))
				.atsInterviewFeedbackLink(interviewDAO.getAtsInterviewFeedbackLink())
				.build();
	}

	private String getMessage(InterviewDAO interviewDAO) {
		EvaluationDAO evaluationDAO = this.evaluationRepository.findById(interviewDAO.getEvaluationId()).get();
		if (EvaluationStatus.WAITING_CLIENT.getValue().equalsIgnoreCase(evaluationDAO.getStatus())) {
			if (this.partnerCompanyRepository.findById(interviewDAO.getPartnerId()).get()
					.getIsCandidateSchedulingEnabled()) {
				return "Candidate is not scheduling despite multiple reminders";
			} else {
				if (Boolean.TRUE.equals(interviewDAO.getIsRescheduled())) {
					String cancellationId = this.interviewHistoryRepository
							.findTopByInterviewIdAndRescheduleCountAndCreatedOnIsNotNullOrderByCreatedOnDesc(
									interviewDAO.getId(), interviewDAO.getRescheduleCount() - 1)
							.getCancellationReasonId();
					return String.format("Rescheduling requested due to \"%s\"",
							this.cancellationReasonRepository.findById(cancellationId).get().getCancellationReason());
				} else {
					return "Interview to be scheduled";
				}
			}
		}
		return null;
	}
}
