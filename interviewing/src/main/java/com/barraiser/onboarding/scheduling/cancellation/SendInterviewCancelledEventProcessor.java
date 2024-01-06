/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation;

import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.interviewcancellation.InterviewCancelledEvent;
import com.barraiser.onboarding.dal.CancellationReasonDAO;
import com.barraiser.onboarding.dal.CancellationReasonRepository;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.events.InterviewingEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.barraiser.onboarding.common.Constants.*;

@Log4j2
@Component
@RequiredArgsConstructor
public class SendInterviewCancelledEventProcessor implements CancellationProcessor {
	private final InterviewingEventProducer eventProducer;
	private final CancellationReasonRepository cancellationReasonRepository;

	@Override
	public void process(final CancellationProcessingData data) throws Exception {
		final InterviewDAO cancelledInterview = data.getPreviousStateOfCancelledInterview();
		if (!Boolean.TRUE.equals(cancelledInterview.getIsRescheduled())) {
			final CancellationReasonDAO cancellationReasonDAO = this.cancellationReasonRepository
					.findById(data.getPreviousStateOfCancelledInterview().getCancellationReasonId()).get();
			final Event<InterviewCancelledEvent> event = new Event<>();
			event.setPayload(new InterviewCancelledEvent()
					.interviewId(data.getInterviewToBeCancelled().getId())
					.cancellationTime(Integer.parseInt(cancelledInterview.getCancellationTime()))
					.partnerId(data.getPartnerId())
					.cancellationReasonId(cancelledInterview.getCancellationReasonId())
					.cancellationType(Objects.equals(cancellationReasonDAO.getCancellationType(),
							CANCELLATION_TYPE_CANDIDATE_AND_EXPERT)
									? CANDIDATE_CANCELLATION_TYPE_FOR_INTERVIEW_CANCELLED_BY_EXPERT_AND_CANDIDATE
									: cancellationReasonDAO.getCancellationType())
					.cancellationReason(Objects.equals(cancellationReasonDAO.getCancellationType(),
							CANCELLATION_TYPE_CANDIDATE_AND_EXPERT)
									? CANDIDATE_REASON_FOR_INTERVIEW_CANCELLED_BY_EXPERT_AND_CANDIDATE
									: cancellationReasonDAO.getCancellationReason())
					.startDate(cancelledInterview.getStartDate().intValue()));
			this.eventProducer.pushEvent(event);
		}
	}
}
