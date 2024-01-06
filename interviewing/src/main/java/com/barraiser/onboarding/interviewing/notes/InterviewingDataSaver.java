/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interviewing.notes;

import com.barraiser.onboarding.interviewing.InterviewingFirestoreData;
import com.barraiser.onboarding.interviewing.InterviewingFirestoreDataFetcher;
import com.barraiser.onboarding.interviewing.notes.dal.InterviewingDataDAO;
import com.barraiser.onboarding.interviewing.notes.dal.InterviewingDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
@Log4j2
public class InterviewingDataSaver {
	private final InterviewingFirestoreDataFetcher InterviewingFirestoreDataFetcher;
	private final InterviewingDataRepository interviewingDataRepository;

	@Transactional
	public void saveInterviewingData(final String interviewId) throws ExecutionException, InterruptedException {
		final InterviewingFirestoreData firestoreData = this.InterviewingFirestoreDataFetcher.get(interviewId);

		final InterviewingDataDAO interviewingDAO = this.getInterviewingData(interviewId);

		if (firestoreData != null && firestoreData.getNotes() != null) {
			this.interviewingDataRepository.save(interviewingDAO.toBuilder()
					.notes(firestoreData.getNotes())
					.interviewFlow(firestoreData.getInterviewFlow())
					.build());
		}
	}

	public void saveWasInterviewerVideoOn(final String interviewId, final Boolean wasInterviewerVideoOn) {
		final InterviewingDataDAO interviewingDAO = this.getInterviewingData(interviewId);
		this.interviewingDataRepository.save(interviewingDAO.toBuilder()
				.wasInterviewerVideoOn(wasInterviewerVideoOn)
				.build());
	}

	private InterviewingDataDAO getInterviewingData(final String interviewId) {
		return this.interviewingDataRepository.findByInterviewId(interviewId).orElse(
				InterviewingDataDAO.builder().id(UUID.randomUUID().toString()).interviewId(interviewId).build());
	}
}
