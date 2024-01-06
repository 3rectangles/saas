/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.common;

import com.barraiser.common.dal.VersionedEntityId;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.InterviewUtil;
import com.barraiser.onboarding.interview.jobrole.SkillWeightageManager;
import com.barraiser.onboarding.scheduling.scheduling.DataValidationProcessor;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Log4j2
@SpringBootTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class DataValidationProcessTest {

	@Mock
	private SkillWeightageManager skillWeightageManager;

	@Mock
	private InterviewUtil interviewUtil;

	@Mock
	private InterviewStructureSkillsRepository interviewStructureSkillsRepository;

	@InjectMocks
	private DataValidationProcessor dataValidationProcessor;

	private final JobRoleDAO jobRoleDAO = JobRoleDAO.builder()
			.entityId(new VersionedEntityId("1", 0))
			.name("a")
			.domainId("b")
			.companyId("c")
			.category("d")
			.minExp(2)
			.maxExp(3)
			.build();

	private final JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO = JobRoleToInterviewStructureDAO
			.builder()
			.id("1")
			.jobRoleId(this.jobRoleDAO.getEntityId().getId())
			.jobRoleVersion(this.jobRoleDAO.getEntityId().getVersion())
			.interviewStructureId("1")
			.build();

	@Test
	public void shouldThrowsExceptionIfSkillWeightageIsEmpty() {
		when(this.skillWeightageManager.getSkillWeightageForJobRole("1", 0))
				.thenReturn(new ArrayList<>());

		when(this.interviewStructureSkillsRepository.findAllByInterviewStructureIdAndIsSpecific("1", false))
				.thenReturn(Stream.of(new InterviewStructureSkillsDAO("1", "2", "2", true, false, null))
						.collect(Collectors.toList()));

		assertThrows(IllegalArgumentException.class, () -> this.dataValidationProcessor
				.checkSkillWeightageExistence(this.jobRoleToInterviewStructureDAO, EvaluationDAO.builder().build()));
	}

	@Test
	public void shouldNotThrowExceptionIfNoSkillWeightageForOthersCategory() {
		when(this.skillWeightageManager.getSkillWeightageForJobRole("1", 0))
				.thenReturn(Stream.of(new SkillWeightageDAO("1", "job", 0, "52", 5.0, "Evaluation"))
						.collect(Collectors.toList()));

		when(this.interviewStructureSkillsRepository.findAllByInterviewStructureIdAndIsSpecific("1", false))
				.thenReturn(Stream
						.of(new InterviewStructureSkillsDAO("1", "is1", Constants.OTHERS_SKILL_ID, false, false, null))
						.collect(Collectors.toList()));

		assertDoesNotThrow(() -> this.dataValidationProcessor
				.checkSkillWeightageExistence(this.jobRoleToInterviewStructureDAO,
						EvaluationDAO.builder().jobRoleId("1").jobRoleVersion(0).build()));
	}

	@Test
	public void shouldThrowExceptionIfSkillWeightageIsNull() {
		when(this.skillWeightageManager.getSkillWeightageForJobRole("1", 0))
				.thenReturn(null);

		when(this.interviewStructureSkillsRepository.findAllByInterviewStructureIdAndIsSpecific("1", false))
				.thenReturn(Stream.of(new InterviewStructureSkillsDAO("1", "2", "2", true, false, null))
						.collect(Collectors.toList()));

		assertThrows(IllegalArgumentException.class, () -> this.dataValidationProcessor
				.checkSkillWeightageExistence(this.jobRoleToInterviewStructureDAO, EvaluationDAO.builder().build()));
	}

	@Test
	public void shouldThrowExceptionIfSkillWeightageAndInterviewStructureSkillsDoNotMatch() throws Exception {

		when(this.skillWeightageManager.getSkillWeightageForJobRole(any(), any()))
				.thenReturn(Stream.of(new SkillWeightageDAO("1", "job", 0, "1", 25.0, "Evaluation"),
						new SkillWeightageDAO("2", "job", 0, "2", 15.0, "Evaluation"))
						.collect(Collectors.toList()));

		when(this.interviewStructureSkillsRepository.findAllByInterviewStructureIdAndIsSpecific("1", false))
				.thenReturn(Stream.of(new InterviewStructureSkillsDAO("1", "2", "2", true, false, null),
						new InterviewStructureSkillsDAO("1", "2", "3", true, false, null))
						.collect(Collectors.toList()));
		assertThrows(IllegalArgumentException.class, () -> this.dataValidationProcessor
				.checkSkillWeightageExistence(this.jobRoleToInterviewStructureDAO, EvaluationDAO.builder().build()));
	}

	@Test
	public void shouldThrowExceptionIfInterviewStructureSkillIsNull() {
		when(this.skillWeightageManager.getSkillWeightageForJobRole(any(), any()))
				.thenReturn(Stream.of(new SkillWeightageDAO("1", "job", 0, "1", 25.0, "Evaluation"),
						new SkillWeightageDAO("2", "job", 0, "2", 15.0, "Evaluation"))
						.collect(Collectors.toList()));

		when(this.interviewStructureSkillsRepository.findAllByInterviewStructureIdAndIsSpecific("1", false))
				.thenReturn(null);

		assertThrows(IllegalArgumentException.class, () -> this.dataValidationProcessor
				.checkSkillWeightageExistence(this.jobRoleToInterviewStructureDAO, EvaluationDAO.builder().build()));
	}

	@Test
	public void shouldThrowExceptionIfInterviewStructureSkillIsEmpty() {
		when(this.skillWeightageManager.getSkillWeightageForJobRole(any(), any()))
				.thenReturn(Stream.of(new SkillWeightageDAO("1", "job", 0, "1", 25.0, "Evaluation"),
						new SkillWeightageDAO("2", "job", 0, "2", 15.0, "Evaluation"))
						.collect(Collectors.toList()));

		when(this.interviewStructureSkillsRepository.findAllByInterviewStructureIdAndIsSpecific("1", false))
				.thenReturn(new ArrayList<>());

		assertThrows(IllegalArgumentException.class, () -> this.dataValidationProcessor
				.checkSkillWeightageExistence(this.jobRoleToInterviewStructureDAO, EvaluationDAO.builder().build()));

	}

	@Test
	public void shouldThrowExceptionIfSkillWeightageHasLesserSkillsThanInterviewStructureToSkill() {
		when(this.skillWeightageManager.getSkillWeightageForJobRole(any(), any()))
				.thenReturn(Stream.of(new SkillWeightageDAO("1", "job", 0, "1", 25.0, "Evaluation"),
						new SkillWeightageDAO("2", "job", 0, "2", 15.0, "Evaluation"))
						.collect(Collectors.toList()));

		when(this.interviewStructureSkillsRepository.findAllByInterviewStructureIdAndIsSpecific("1", false))
				.thenReturn(Stream.of(new InterviewStructureSkillsDAO("1", "2", "2", true, false, null),
						new InterviewStructureSkillsDAO("2", "3", "1", true, false, null),
						new InterviewStructureSkillsDAO("3", "1", "3", true, false, null))
						.collect(Collectors.toList()));
		assertThrows(IllegalArgumentException.class, () -> this.dataValidationProcessor
				.checkSkillWeightageExistence(this.jobRoleToInterviewStructureDAO, EvaluationDAO.builder().build()));
	}

	@Test
	public void shouldNotThrowExceptionIfInterviewStructureSkillsNotPresentForAllSkillWeightages() {

		when(this.skillWeightageManager.getSkillWeightageForJobRole(any(), any())).thenReturn(List.of(
				new SkillWeightageDAO("1", "job", 0, "1", 25.0, "Evaluation"),
				new SkillWeightageDAO("2", "job", 0, "2", 15.0, "Evaluation"),
				new SkillWeightageDAO("3", "job", 0, "3", 15.0, "Evaluation")));

		when(this.interviewStructureSkillsRepository.findAllByInterviewStructureIdAndIsSpecific("1", false))
				.thenReturn(Stream.of(new InterviewStructureSkillsDAO("1", "2", "2", true, false, null),
						new InterviewStructureSkillsDAO("2", "3", "3", true, false, null))
						.collect(Collectors.toList()));

		assertDoesNotThrow(() -> this.dataValidationProcessor
				.checkSkillWeightageExistence(this.jobRoleToInterviewStructureDAO, EvaluationDAO.builder().build()));
	}

	@Test
	public void shouldNotThrowExceptionIfSkillWeightageEqualsInterviewStructureToSkill() {

		when(this.skillWeightageManager.getSkillWeightageForJobRole(any(), any()))
				.thenReturn(List.of(new SkillWeightageDAO("1", "job", 0, "1", 25.0, "Evaluation"),
						new SkillWeightageDAO("2", "job", 0, "2", 15.0, "Evaluation")));

		when(this.interviewStructureSkillsRepository.findAllByInterviewStructureIdAndIsSpecific("1", false))
				.thenReturn(Stream.of(new InterviewStructureSkillsDAO("1", "4", "1", true, false, null),
						new InterviewStructureSkillsDAO("2", "3", "2", true, false, null))
						.collect(Collectors.toList()));

		assertDoesNotThrow(() -> this.dataValidationProcessor
				.checkSkillWeightageExistence(this.jobRoleToInterviewStructureDAO, EvaluationDAO.builder()
						.build()));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionIfCandidateHasOverlappingBookedSlot() {
		when(this.interviewUtil.getOverlappingInterviewsForCandidate("1", 1637613000L, 1637616600L))
				.thenReturn(List.of(InterviewDAO.builder().startDate(1637613000L).endDate(1637616600L).build()));
		this.dataValidationProcessor.checkIfCandidateHasOverlappingBookedSlot("1", 1637613000L, 1637616600L);
	}

	@Test
	public void shouldNotThrowExceptionIfCandidateHasNoOverlappingBookedSlot() {
		when(this.interviewUtil.getOverlappingInterviewsForCandidate("1", 1637613000L, 1637616600L))
				.thenReturn(List.of());
		this.dataValidationProcessor.checkIfCandidateHasOverlappingBookedSlot("1", 1637613000L, 1637616600L);
	}
}
