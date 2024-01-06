/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.dal.specifications.InterviewSpecifications;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NormalisedRatingPopulatorTest {

	@Mock
	private ExpertNormalisedRatingRepository expertNormalisedRatingRepository;
	@Mock
	private FeedbackRepository feedbackRepository;
	@Mock
	private QuestionRepository questionRepository;
	@Mock
	private ObjectMapper objectMapper;
	@Mock
	private InterViewRepository interViewRepository;
	@Mock
	private InterviewSpecifications interviewSpecifications;
	@InjectMocks
	private NormalisedRatingPopulator normalisedRatingPopulator;
	@Mock
	private Specification<InterviewDAO> s1;

	@Test
	public void getRatingToNormalisedRatingWhenNumberOfInterviewsIsLessThan5() {
		when(this.expertNormalisedRatingRepository
				.findAllByInterviewerId("1"))
						.thenReturn(
								List.of(ExpertNormalisedRatingDAO.builder().rating(1D).normalisedRating(1.7D).build(),
										ExpertNormalisedRatingDAO.builder().rating(2D).normalisedRating(1.2D).build(),
										ExpertNormalisedRatingDAO.builder().rating(3D).normalisedRating(3D).build(),
										ExpertNormalisedRatingDAO.builder().rating(4D).normalisedRating(4D).build(),
										ExpertNormalisedRatingDAO.builder().rating(5D).normalisedRating(5.9D).build(),
										ExpertNormalisedRatingDAO.builder().rating(6D).normalisedRating(9D).build(),
										ExpertNormalisedRatingDAO.builder().rating(7D).normalisedRating(7D).build(),
										ExpertNormalisedRatingDAO.builder().rating(8D).normalisedRating(8D).build(),
										ExpertNormalisedRatingDAO.builder().rating(9D).normalisedRating(9D).build(),
										ExpertNormalisedRatingDAO.builder().rating(10D).normalisedRating(1D).build()));
		when(this.interviewSpecifications.getExpertsInterviewsSpecification("1", null,
				null, List.of("Done"), null)).thenReturn(s1);
		final List<InterviewDAO> interviews1 = new ArrayList<>();
		for (int i = 1; i <= 5; i++) {
			interviews1.add(InterviewDAO.builder().id("" + i).interviewerId("1").startDate(1L).build());
		}
		when(this.interViewRepository.findAll(eq(s1),
				(Pageable) argThat(
						arg -> ((Pageable) arg).getPageNumber() == 0 && ((Pageable) arg).getPageSize() == 15)))
								.thenReturn(new PageImpl(interviews1));
		when(this.interviewSpecifications.hasStartDateNotNull()).thenReturn(s1);
		when(s1.and(any())).thenReturn(s1);
		final List<NormalisedRatingMapping> actualResult = this.normalisedRatingPopulator
				.getRatingToNormalisedRatingOfExpert("1");
		final List<NormalisedRatingMapping> expectedResult = List.of(
				NormalisedRatingMapping.builder().rating(1F).cappedNormalisedRating(1.7F).build(),
				NormalisedRatingMapping.builder().rating(2F).cappedNormalisedRating(1.2F).build(),
				NormalisedRatingMapping.builder().rating(3F).cappedNormalisedRating(3F).build(),
				NormalisedRatingMapping.builder().rating(4F).cappedNormalisedRating(4F).build(),
				NormalisedRatingMapping.builder().rating(5F).cappedNormalisedRating(5.9F).build(),
				NormalisedRatingMapping.builder().rating(6F).cappedNormalisedRating(9F).build(),
				NormalisedRatingMapping.builder().rating(7F).cappedNormalisedRating(7F).build(),
				NormalisedRatingMapping.builder().rating(8F).cappedNormalisedRating(8F).build(),
				NormalisedRatingMapping.builder().rating(9F).cappedNormalisedRating(9F).build(),
				NormalisedRatingMapping.builder().rating(10F).cappedNormalisedRating(1F).build());
		expectedResult
				.forEach(x -> assertEquals(x.getCappedNormalisedRating(), actualResult.get(x.getRating().intValue())));
	}
}
