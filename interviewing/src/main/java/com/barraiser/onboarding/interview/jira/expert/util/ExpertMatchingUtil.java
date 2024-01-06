/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.jira.expert.util;

import com.barraiser.onboarding.interview.jira.expert.EligibleExpertsClassificationException;
import com.barraiser.onboarding.interview.jobrole.JobRoleCategory;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.NoSuchElementException;

@Log4j2
@AllArgsConstructor
@Component
public class ExpertMatchingUtil {
	private final String EXPERT = "EXPERT";
	private final String INVALID_CATEGORY = "Not a valid category";

	private final EligibleExpertsClassificationException eligibleExpertsClassificationException;

	public Integer getRequiredWorkExperienceOfExpert(
			final String categoryOfJobRole,
			final Integer workExperienceOfInterviewee,
			final String interviewRound, final String partnerId) {
		final JobRoleCategory category = JobRoleCategory.valueOf(categoryOfJobRole);

		switch (category) {
			case A:
				// this is just a hack for recro
				if (this.eligibleExpertsClassificationException.includeWorkExperienceFilterForScheduling(partnerId)) {
					return null;
				}
				return workExperienceOfInterviewee <= 48 ? workExperienceOfInterviewee : 48;
			case B:
				return (workExperienceOfInterviewee <= 72 ? workExperienceOfInterviewee : 72);
			case C:
				return (EXPERT.equals(interviewRound)
						? workExperienceOfInterviewee <= 108 ? workExperienceOfInterviewee : 96
						: workExperienceOfInterviewee <= 108
								? workExperienceOfInterviewee - 24
								: 84);
			case D:
				return (EXPERT.equals(interviewRound) ? null : workExperienceOfInterviewee - 24);
			case E:
			case F:
				return (EXPERT.equals(interviewRound) ? null : workExperienceOfInterviewee - 36);
			default:
				throw new NoSuchElementException(INVALID_CATEGORY);
		}
	}

	public List<String> getRequiredCategoryOfExpert(
			final String categoryOfJobRole, final String interviewRound) {
		final JobRoleCategory category = JobRoleCategory.valueOf(categoryOfJobRole);
		switch (category) {
			case A:
				return List.of(JobRoleCategory.A.getValue(), JobRoleCategory.B.getValue(),
						JobRoleCategory.C.getValue());
			case B:
				return (EXPERT.equals(interviewRound)
						? List.of(JobRoleCategory.B.getValue(), JobRoleCategory.C.getValue(),
								JobRoleCategory.D.getValue())
						: List.of(JobRoleCategory.A.getValue(), JobRoleCategory.B.getValue(),
								JobRoleCategory.C.getValue(), JobRoleCategory.D.getValue()));
			case C:
				return (EXPERT.equals(interviewRound)
						? List.of(JobRoleCategory.C.getValue(), JobRoleCategory.D.getValue(),
								JobRoleCategory.E.getValue(), JobRoleCategory.F.getValue())
						: List.of(JobRoleCategory.B.getValue(), JobRoleCategory.C.getValue(),
								JobRoleCategory.D.getValue(), JobRoleCategory.E.getValue(),
								JobRoleCategory.F.getValue()));
			case D:
				return (EXPERT.equals(interviewRound)
						? List.of(JobRoleCategory.D.getValue(), JobRoleCategory.E.getValue(),
								JobRoleCategory.F.getValue())
						: List.of(JobRoleCategory.C.getValue(), JobRoleCategory.D.getValue(),
								JobRoleCategory.E.getValue(), JobRoleCategory.F.getValue()));
			case E:
				return (EXPERT.equals(interviewRound)
						? List.of(JobRoleCategory.E.getValue(), JobRoleCategory.F.getValue())
						: List.of(JobRoleCategory.D.getValue(), JobRoleCategory.E.getValue(),
								JobRoleCategory.F.getValue()));
			case F:
				return (EXPERT.equals(interviewRound) ? List.of(JobRoleCategory.F.getValue())
						: List.of(JobRoleCategory.E.getValue(), JobRoleCategory.F.getValue()));
			default:
				throw new NoSuchElementException(INVALID_CATEGORY);
		}
	}

}
