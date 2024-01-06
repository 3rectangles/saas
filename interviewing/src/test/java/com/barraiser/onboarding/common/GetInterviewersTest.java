/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.common;

import com.barraiser.onboarding.dal.EvaluationRepository;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.UserDetailsDAO;
import com.barraiser.onboarding.dal.UserDetailsRepository;
import com.barraiser.onboarding.interview.GetInterviewers;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.search.InterviewerMatcher;
import lombok.extern.log4j.Log4j2;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.when;

@Ignore
@Log4j2
@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class GetInterviewersTest {

	@Mock
	private InterViewRepository interViewRepository;

	@Mock
	private UserDetailsRepository userDetailsRepository;

	@InjectMocks
	private GetInterviewers getInterviewers;

	@Test
	public void testGetInterviewerCase1() {

		// size of array matches expected value

		final InterviewDAO interviewDAO1 = InterviewDAO.builder()
				.id("1")
				.interviewerId("a")
				.build();
		final InterviewDAO interviewDAO2 = InterviewDAO.builder()
				.id("2")
				.interviewerId("b")
				.build();

		when(this.interViewRepository.findAllByEvaluationId("e1"))
				.thenReturn(Stream.of(interviewDAO1, interviewDAO2).collect(Collectors.toList()));

		List<String> interviewers = Stream.of("a", "c", "d", "e").collect(Collectors.toList());

		assertEquals(3, this.getInterviewers.getAllUnusedInterviewers("e1", interviewers).size());
	}

	@Test
	public void testGetInterviewerCase2() {
		// size of array matches expected value

		final InterviewDAO interviewDAO1 = InterviewDAO.builder()
				.id("1")
				.interviewerId("a")
				.build();
		final InterviewDAO interviewDAO2 = InterviewDAO.builder()
				.id("2")
				.interviewerId("b")
				.build();

		when(this.interViewRepository.findAllByEvaluationId("e1"))
				.thenReturn(Stream.of(interviewDAO1, interviewDAO2).collect(Collectors.toList()));

		List<String> interviewers = Stream.of("t", "r", "d", "e").collect(Collectors.toList());

		assertNotEquals(3, this.getInterviewers.getAllUnusedInterviewers("e1", interviewers).size());
		assertEquals(4, this.getInterviewers.getAllUnusedInterviewers("e1", interviewers).size());
	}

	@Test
	public void testGetInterviewerCase3() {
		// used Interviewers are Empty

		final InterviewDAO interviewDAO1 = InterviewDAO.builder()
				.id("1")
				.interviewerId("a")
				.build();
		final InterviewDAO interviewDAO2 = InterviewDAO.builder()
				.id("2")
				.interviewerId("b")
				.build();

		when(this.interViewRepository.findAllByEvaluationId("e1"))
				.thenReturn(new ArrayList<>());

		List<String> interviewers = Stream.of("a", "b", "d", "e").collect(Collectors.toList());

		assertEquals(4, this.getInterviewers.getAllUnusedInterviewers("e1", interviewers).size());

	}

	@Test
	public void getInterviewersInSortedOrderTest() {
		List<String> interviewers = Stream.of("id1", "id2", "id3").collect(Collectors.toList());
		final UserDetailsDAO userDetailsDAO1 = UserDetailsDAO.builder()
				.id("id1")
				.firstName("Z")
				.build();
		final UserDetailsDAO userDetailsDAO2 = UserDetailsDAO.builder()
				.id("id2")
				.firstName("P")
				.build();
		final UserDetailsDAO userDetailsDAO3 = UserDetailsDAO.builder()
				.id("id3")
				.firstName("A")
				.build();
		List<UserDetailsDAO> userDetailsDAOList = new ArrayList<>(
				Arrays.asList(userDetailsDAO1, userDetailsDAO2, userDetailsDAO3));

		when(this.userDetailsRepository.findAllByIdIn(interviewers))
				.thenReturn(userDetailsDAOList);
		assertEquals("id3", this.getInterviewers.getInterviewersInSortedOrder(userDetailsDAOList).get(0));
		assertEquals("id2", this.getInterviewers.getInterviewersInSortedOrder(userDetailsDAOList).get(1));
		assertEquals("id1", this.getInterviewers.getInterviewersInSortedOrder(userDetailsDAOList).get(2));
	}
}
