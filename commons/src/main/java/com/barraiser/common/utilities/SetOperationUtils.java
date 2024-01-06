/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.utilities;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class SetOperationUtils {

	public static <T> Set<T> findDifference(Set<T> setA, Set<T> setB) {
		Set<T> difference = new HashSet<>(setA);
		difference.removeAll(setB);
		return difference;
	}

	public static <T> Set<T> findUnion(Set<T> setA, Set<T> setB) {
		Set<T> union = new HashSet<>(setA);
		union.addAll(setB);
		return union;
	}

}
