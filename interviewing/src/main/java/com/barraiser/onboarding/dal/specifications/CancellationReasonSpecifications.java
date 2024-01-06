package com.barraiser.onboarding.dal.specifications;

import com.barraiser.onboarding.dal.CancellationReasonDAO;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Path;
import java.util.List;

@Component
public class CancellationReasonSpecifications {

    public Specification<CancellationReasonDAO> hasCancellationTypeIn(final List<String> cancellationTypes) {
        return (Specification<CancellationReasonDAO>) (root, criteriaQuery, criteriaBuilder) -> {
            final Path<String> g = root.get("cancellationType");
            return g.in(cancellationTypes);
        };
    }

    public Specification<CancellationReasonDAO> hasProcessType(final String processType) {
        return ((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("processType"), processType));
    }

    public Specification<CancellationReasonDAO> hasIsActiveFlag(final Boolean isActive) {
        return ((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("isActive"), isActive));
    }

    public Specification<CancellationReasonDAO> getCancellationReasonSpecifications(final List<String> types, final Boolean isActive, final String processType) {
        Specification<CancellationReasonDAO> specifications = null;

        if (isActive != null) {
            specifications = Specification.where(this.hasIsActiveFlag(isActive));
        } else {
            specifications = Specification.where(this.hasIsActiveFlag(Boolean.TRUE));
        }

        if (types != null && !types.isEmpty()) {
            specifications = specifications.and(this.hasCancellationTypeIn(types));
        }

        if (processType != null) {
            specifications = specifications.and(this.hasProcessType(processType));
        }

        return specifications;
    }

}
