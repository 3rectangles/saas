package com.barraiser.onboarding.common.search.db;

import com.barraiser.onboarding.dal.EvaluationDAO;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class SearchAggregator {
    @PersistenceContext
    private final EntityManager em;

    public <T> AggregationResult aggregateCount(
        final Specification<T> originalSpecification,
        final SearchAggregation searchAggregation,
        final Class T
    ) {
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> q = cb.createQuery(Object[].class);
        final Root<T> root = q.from(T);
        Predicate predicate = originalSpecification.toPredicate(root, q, cb);
        if(searchAggregation.getFilters() != null) {
            final Specification<T> filtersSpecification = SearchSpecificationBuilder.getSpecificationForFilters(searchAggregation.getFilters());
            predicate = cb.and(predicate, filtersSpecification.toPredicate(root, q, cb));
        }


        final Path expression = this.getExpression(searchAggregation, root);

        q.multiselect(expression, cb.count(expression));
        q = q.where(predicate);
        q.groupBy(expression);
        TypedQuery<Object[]> typedQuery = em.createQuery(q);
        List<Object[]> result = typedQuery.getResultList();

        return AggregationResult.builder()
            .aggregatedCount(result.stream().map(r -> AggregationResult.AggregationCountResult.builder()
                .fieldValue(r[0])
                .count((Long) r[1])
                .build()).collect(Collectors.toList()))
            .name(searchAggregation.getName())
            .build();
    }



    private <T> Path getExpression(final SearchAggregation searchAggregation, final Root<T> root) {
        Join<Object, Object> joinParent = null;
        if(searchAggregation.getPath() != null) {
            for(final String joinColumn: searchAggregation.getPath()) {
                joinParent = joinParent != null ? joinParent.join(joinColumn) : root.join(joinColumn);
            }
        }

        final Path expression = joinParent == null ?
            root.get(searchAggregation.getField()) :
            joinParent.get(searchAggregation.getField());

        return expression;
    }
}
