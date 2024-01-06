/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class QueryExecutor {
	private final JdbcTemplate jdbcTemplate;

	public QueryExecutor(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public Map<String, String> executeQuery(String query) {
		if (!isReadQuery(query)) {
			throw new IllegalArgumentException("Only read queries are allowed.");
		}
		List<Map<String, String>> results = jdbcTemplate.query(query, new QueryResultMapper());

		if (results.isEmpty()) {
			return null; // or throw an exception if required
		}

		return results.get(0);
	}

	private boolean isReadQuery(String query) {
		String trimmedQuery = query.trim().toLowerCase();
		if (trimmedQuery.contains("drop ") || trimmedQuery.contains("update "))
			return false;
		return trimmedQuery.startsWith("select");
	}

	public Map<String, String> executeParametrizedQuery(String query, Map<String, String> params) {
		String processedQuery = processQueryParams(query, params);
		return executeQuery(processedQuery);
	}

	private String processQueryParams(String query, Map<String, String> params) {
		for (Map.Entry<String, String> entry : params.entrySet()) {
			String paramName = entry.getKey();
			String paramValue = entry.getValue();
			String placeholder = ":" + paramName;
			query = query.replace(placeholder, paramValue);
		}
		return query;
	}

	private static class QueryResultMapper implements RowMapper<Map<String, String>> {
		@Override
		public Map<String, String> mapRow(ResultSet rs, int rowNum) throws SQLException {
			Map<String, String> resultMap = new HashMap<>();
			int columnCount = rs.getMetaData().getColumnCount();

			for (int i = 1; i <= columnCount; i++) {
				String columnName = rs.getMetaData().getColumnName(i);
				String columnValue = rs.getString(i);
				resultMap.put(columnName, columnValue);
			}

			return resultMap;
		}
	}
}
