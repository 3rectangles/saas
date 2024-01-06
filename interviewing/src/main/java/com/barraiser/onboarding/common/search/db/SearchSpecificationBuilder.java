/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.common.search.db;

import com.barraiser.commons.auth.SearchFilter;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Log4j2
@Component
@AllArgsConstructor
public class SearchSpecificationBuilder {
	public static <T> Specification<T> getSpecificationForFilters(final List<SearchFilter> filters) {
		return (root, query, cb) -> {
			final List<Predicate> predicates = new ArrayList<>();

			for (final SearchFilter filter : filters) {
				predicates.add(getFilterPredicate(filter, cb, root));
			}

			if (predicates.isEmpty()) {
				return cb.conjunction();
			}

			return cb.and(predicates.toArray(Predicate[]::new));
		};
	}

	private static <T> Predicate getFilterPredicate(final SearchFilter filter, final CriteriaBuilder cb,
			final Root<T> root) {
		if (filter.getMatchAnyOf() != null) {
			final List<Predicate> matchAnyPredicates = new ArrayList<>();
			for (final SearchFilter matchAnyFilter : filter.getMatchAnyOf()) {
				matchAnyPredicates.add(getFilterPredicate(matchAnyFilter, cb, root));
			}
			return cb.or(matchAnyPredicates.toArray(Predicate[]::new));
		} else if (filter.getMatchAll() != null) {
			final List<Predicate> matchAllPredicates = new ArrayList<>();
			for (final SearchFilter matchAllFilter : filter.getMatchAll()) {
				matchAllPredicates.add(getFilterPredicate(matchAllFilter, cb, root));
			}
			return cb.and(matchAllPredicates.toArray(Predicate[]::new));
		} else {
			final Path expression = getExpression(filter, root);
			return getPredicate(filter, cb, expression);
		}
	}

	private static <T> Path getExpression(final SearchFilter filter, final Root<T> root) {
		Join<Object, Object> joinParent = null;
		if (filter.getPath() != null) {
			for (final String joinColumn : filter.getPath()) {
				joinParent = joinParent != null ? joinParent.join(joinColumn, JoinType.LEFT)
						: root.join(joinColumn, JoinType.LEFT);
			}
		}

		final Path expression = joinParent == null ? root.get(filter.getField()) : joinParent.get(filter.getField());
		return expression;
	}

	private static Predicate getPredicate(final SearchFilter filter,
			final CriteriaBuilder cb, final Path expression) {
		switch (filter.getOperator()) {
			case EQUALS:
				return cb.equal(expression, filter.getValue());
			case LIKE:
				return cb.like(expression, "%" + filter.getValue() + "%");
			case LIKE_IGNORE_CASE:
				final String valueInLowerCase = filter.getValue().toString().toLowerCase();
				return cb.like(cb.lower(expression), "%" + valueInLowerCase + "%");
			case IN:
				return cb.in(expression).value(filter.getValue());
			case GREATER_THAN:
				return cb.greaterThan(expression, (Comparable) filter.getValue());
			case LESS_THAN:
				return cb.lessThan(expression, (Comparable) filter.getValue());
			case GREATER_THAN_OR_EQUAL_TO:
				return cb.greaterThanOrEqualTo(expression, (Comparable) filter.getValue());
			case LESS_THAN_OR_EQUAL_TO:
				return cb.lessThanOrEqualTo(expression, (Comparable) filter.getValue());
			case NOT_EQUALS:
				return cb.notEqual(expression, filter.getValue());
			case IS_NULL:
				return cb.isNull(expression);
			case IS_NOT_NULL:
				return cb.isNotNull(expression);
			case CONTAINS:
				return cb.equal(cb.literal(filter.getValue()), cb.function("ANY",
						Boolean.class, expression));
			default:
				throw new NoSuchElementException(
						"Filter Operator with value " + filter.getOperator() + " has not been found");
		}
	}
}
