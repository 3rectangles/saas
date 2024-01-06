/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.utilities;

import com.barraiser.common.monitoring.Profiled;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Log4j2
public class ObjectFieldsFilter<T> {
	/**
	 * This method is responsible for filtering out the properties from an object.
	 * We are using reflection here as other
	 * options seemed more expensive theoretically.
	 * The method would run for all objects that are being returned by GraphQL and
	 * hence we need to do all kinds of
	 * optimization possible.
	 *
	 * @param object
	 *            the object which needs to be cleaned.
	 * @param fieldsToRetain
	 *            list of field names that would be retained.
	 * @return T
	 */

	@Profiled(name = "objectFieldFilter")
	public T filter(final T object, final List<String> fieldsToRetain) throws IllegalAccessException {
		if (fieldsToRetain.get(0).equals("*")) {
			return object;
		}
		if (object instanceof List<?>) {
			final List<?> collection = (List<?>) object;
			final List<Object> filteredCollection = new ArrayList<>();
			for (Object o : collection) {
				filteredCollection.add(this.filterObject(o, fieldsToRetain));
			}
			return (T) filteredCollection;
		}
		return filterAnObject(object, fieldsToRetain);
	}

	private Object filterObject(Object object, List<String> fieldsToRetain) throws IllegalAccessException {
		final Field[] fields = object.getClass().getDeclaredFields();
		// The following if condition could be costly operation say an object contains
		// 10 fields.
		// Say we are returning objects of three hirarchy - the total number of checks
		// could easily go to 10*10*10
		final Map<String, Boolean> fieldMap = fieldsToRetain.stream()
				.collect(Collectors.toMap(field -> field, field -> Boolean.TRUE));

		for (final Field field : fields) {
			if (!Boolean.TRUE.equals(fieldMap.get(field.getName()))) {
				this.unsetFields(object, field);
			}
		}

		return object;
	}

	private T filterAnObject(T object, List<String> fieldsToRetain) throws IllegalAccessException {
		final Field[] fields = object.getClass().getDeclaredFields();
		// The following if condition could be costly operation say an object contains
		// 10 fields.
		// Say we are returning objects of three hirarchy - the total number of checks
		// could easily go to 10*10*10
		final Map<String, Boolean> fieldMap = fieldsToRetain.stream()
				.collect(Collectors.toMap(field -> field, field -> Boolean.TRUE));

		for (final Field field : fields) {
			if (!Boolean.TRUE.equals(fieldMap.get(field.getName()))) {
				this.unsetFields(object, field);
			}
		}

		return object;
	}

	private void unsetFields(Object object, Field field) throws IllegalAccessException {
		field.setAccessible(true);

		/**
		 * Reflection cannot set primitive types to null, hence we are choosing to set
		 * the default values of the
		 * primitives. We try not to use primitives in our GraphQL types.
		 */
		Class<?> fieldType = field.getType();
		if (fieldType.equals(short.class)) {
			field.set(object, 0);
		} else if (fieldType.equals(int.class)) {
			field.set(object, 0);
		} else if (fieldType.equals(char.class)) {
			field.set(object, 0);
		} else if (fieldType.equals(long.class)) {
			field.set(object, 0L);
		} else if (fieldType.equals(byte.class)) {
			field.set(object, 0);
		} else if (fieldType.equals(float.class)) {
			field.set(object, 0.0f);
		} else if (fieldType.equals(double.class)) {
			field.set(object, 0.0d);
		} else if (fieldType.equals(boolean.class)) {
			field.set(object, false);
		} else {
			field.set(object, null);
		}
		field.setAccessible(false);
	}
}
