package com.barraiser.onboarding.dal.specifications;

import com.barraiser.onboarding.dal.InterviewHistoryDAO;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;

import javax.persistence.criteria.Path;

@Log4j2
@Component
@AllArgsConstructor
public class InterviewHistorySpecifications {

    public Specification<InterviewHistoryDAO> getExpertsInterviewsSpecification(
            final String interviewerId,
            Long startTimeStamp,
            Long endTimeStamp,
            List<String> includedStatuses,
            List<String> excludedStatuses) {

        Specification<InterviewHistoryDAO> specifications = null;
        if (startTimeStamp != null) {
            specifications = Specification.where(this.hasStartDateGreaterThan(startTimeStamp));
        }
        if (interviewerId != null) {
            if (specifications == null) {
                specifications = Specification.where(this.hasInterviewerId(interviewerId));
            } else {
                specifications = specifications.and(this.hasInterviewerId(interviewerId));
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

    public Specification<InterviewHistoryDAO> hasStartDateGreaterThan(Long timestamp) {
        return ((root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), timestamp));
    }

    public Specification<InterviewHistoryDAO> hasStartDateLessThan(Long timestamp) {
        return ((root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.lessThan(root.get("startDate"), timestamp));
    }

    public Specification<InterviewHistoryDAO> hasInterviewerId(final String interviewerId) {
        return ((root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("interviewerId"), interviewerId));
    }

    private Specification<InterviewHistoryDAO> hasStatusIn(final List<String> statusList) {
        return (Specification<InterviewHistoryDAO>)
                (root, criteriaQuery, criteriaBuilder) -> {
                    final Path<String> g = root.get("status");
                    return g.in(statusList);
                };
    }

    private Specification<InterviewHistoryDAO> hasStatusNotIn(final List<String> statusList) {
        return (Specification<InterviewHistoryDAO>)
                (root, criteriaQuery, criteriaBuilder) -> {
                    final Path<String> g = root.get("status");
                    return g.in(statusList).not();
                };
    }

    public Specification<InterviewHistoryDAO> excludeDuplicateInterview() {
        return ((root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.isNull(root.get("duplicateReason")));
    }
}
