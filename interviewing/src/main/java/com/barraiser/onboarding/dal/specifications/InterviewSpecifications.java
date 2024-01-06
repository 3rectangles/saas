/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal.specifications;

import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.common.graphql.input.GetInterviewsInput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Path;
import java.util.List;

@Component
public class InterviewSpecifications {

	public Specification<InterviewDAO> hasStatus(final String status) {
		return (root, query, cb) -> cb.equal(root.get("status"), status);
	}

	public Specification<InterviewDAO> hasId(final String id) {
		return (root, query, cb) -> cb.equal(root.get("id"), id);
	}

	public Specification<InterviewDAO> hasInterviewerId(final String interviewerId) {
		return ((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("interviewerId"),
				interviewerId));
	}

	private Specification<InterviewDAO> hasStatusIn(final List<String> statusList) {
		return (Specification<InterviewDAO>) (root, criteriaQuery, criteriaBuilder) -> {
			final Path<String> g = root.get("status");
			return g.in(statusList);
		};
	}

	private Specification<InterviewDAO> hasStatusNotIn(final List<String> statusList) {
		return (Specification<InterviewDAO>) (root, criteriaQuery, criteriaBuilder) -> {
			final Path<String> g = root.get("status");
			return g.in(statusList).not();
		};
	}

	public Specification<InterviewDAO> hasStartDateGreaterThan(Long timestamp) {
		return ((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"),
				timestamp));
	}

	public Specification<InterviewDAO> hasStartDateLessThan(Long timestamp) {
		return ((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.lessThan(root.get("startDate"), timestamp));
	}

	public Specification<InterviewDAO> hasPartnerId(String partnerId) {
		return (root, query, criteriaBuilder) -> {
			if (partnerId == null) {
				return criteriaBuilder.conjunction(); // An empty condition (no filtering)
			} else {
				return criteriaBuilder.equal(root.get("partnerId"), partnerId);
			}
		};
	}

	public Specification<InterviewDAO> getInterviewDAOSpecification(
			final GetInterviewsInput input,
			String interviewId) {
		Specification<InterviewDAO> specifications = null;

		if (input.getStatus() != null) {
			specifications = Specification.where(this.hasStatus(input.getStatus()));
		}

		if (interviewId != null) {
			if (specifications != null) {
				specifications = specifications.and(this.hasId(interviewId));
			} else {
				specifications = this.hasId(interviewId);
			}
		}
		return specifications;
	}

	public Specification<InterviewDAO> excludeDuplicateInterview() {
		return ((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.isNull(root.get("duplicateReason")));
	}

	public Specification<InterviewDAO> getExpertsInterviewsSpecification(
			final String interviewerId, Long startTimeStamp, Long endTimeStamp, List<String> includedStatuses,
			List<String> excludedStatuses) {

		Specification<InterviewDAO> specifications = null;
		if (startTimeStamp != null) {
			specifications = Specification.where(this.hasStartDateGreaterThan(startTimeStamp));
		}
		if (interviewerId != null) {
			if (specifications == null) {
				specifications = Specification.where(this.hasInterviewerId(interviewerId));
			} else {
				specifications = specifications.and(this.hasInterviewerId(interviewerId));
			}
		} else {
			if (specifications == null) {
				specifications = Specification.where(this.hasInterviewerIdNotNull());
			} else {
				specifications = specifications.and(this.hasInterviewerIdNotNull());
			}
		}
		if (endTimeStamp != null) {
			specifications = specifications.and(this.hasStartDateLessThan(endTimeStamp));
		}
		if (includedStatuses != null) {
			specifications = specifications.and(this.hasStatusIn(includedStatuses));
		}
		if (excludedStatuses != null) {
			specifications = specifications.and(this.hasStatusNotIn(excludedStatuses));
		}
		specifications = specifications.and(this.excludeDuplicateInterview());
		return specifications;
	}

	public Specification<InterviewDAO> hasStartDateNotNull() {
		return ((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.isNotNull(root.get("startDate")));
	}

	public Specification<InterviewDAO> getUpcomingInterviewsSpecification(
			final String partnerId,
			final String interviewerId,
			final Long timestamp,
			final List<String> excludedStatuses) {
		Specification<InterviewDAO> specifications = null;
		if (interviewerId != null) {
			specifications = Specification.where(this.hasInterviewerId(interviewerId));
		}

		if (timestamp != null) {
			specifications = (specifications != null) ? specifications.and(this.hasStartDateGreaterThan(timestamp))
					: this.hasStartDateGreaterThan(timestamp);
		}

		if (partnerId != null) {
			specifications = (specifications != null) ? specifications.and(this.hasPartnerId(partnerId))
					: this.hasPartnerId(partnerId);
		}

		if (excludedStatuses != null) {
			specifications = specifications.and(this.hasStatusNotIn(excludedStatuses));
		}

		specifications = specifications.and(this.hasStartDateNotNull());

		return specifications;
	}

	public Specification<InterviewDAO> hasEndDateGreaterThan(final Long timestamp) {
		return ((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("endDate"),
				timestamp));
	}

	private Specification<InterviewDAO> hasInterviewerIdNotNull() {
		return ((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.isNotNull(root.get("interviewerId")));
	}
}
