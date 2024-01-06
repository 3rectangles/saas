/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.sfn.async;

public interface StepFunctionActivity<T> {
	String name();

	/**
	 * Why input is a String type? The reason is we have migrated the core
	 * StepFunction processing inside @{@link StepFunction} class which
	 * has a generic type of T. But Jackson (AFAIK) cannot deserialize to
	 * T. At the max, it can deserialize a class/pojo with no hierarchical
	 * data. The moment T becomes a class with properties which are not
	 * primitive types, Jackson fails for some reason. If we can figure out
	 * a way to map String to T in @{@link StepFunction}, we would
	 * be able to achieve absolute Strong typing, till then, every
	 * implementation of activity will have to map the input String to its
	 * generic type T.
	 */
	T process(String input) throws Exception;
}
