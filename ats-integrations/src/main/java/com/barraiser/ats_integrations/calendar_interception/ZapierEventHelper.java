/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.calendar_interception;

import com.barraiser.commons.eventing.schema.commons.calendar.BRCalendarEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Arrays;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
@Log4j2
public class ZapierEventHelper {

	private static final String INTERCEPTION_FAILURE_ZAP_URL = "https://hooks.zapier.com/hooks/catch/16484716/3iil9h8/";
	private final Environment environment;
	private final ObjectMapper objectMapper;

	@SneakyThrows
	public void sendExceptionToZap(final BRCalendarEvent event, final Exception e) {
		final String environmentProfile = Arrays.stream(this.environment.getActiveProfiles()).findFirst().get();
		final String calendarEventJsonString = objectMapper.writeValueAsString(event);

		final JSONObject payload = new JSONObject();
		payload.put("ExceptionStackTrace", this.getStackTraceString(e));
		payload.put("Environment", environmentProfile);
		payload.put("Event", new JSONObject(calendarEventJsonString));

		// Sending the calendar event and exception data to Zapier
		HttpURLConnection connection = (HttpURLConnection) new URL(INTERCEPTION_FAILURE_ZAP_URL).openConnection();
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type", "application/json");
		OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
		writer.write(payload.toString());
		writer.flush();

		final int responseCode = connection.getResponseCode();
		if (responseCode < 200 && responseCode >= 400) {
			throw new RuntimeException("Failure in sending Zap");
		}
	}

	private String getStackTraceString(Exception E) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		E.printStackTrace(pw);
		return sw.toString();
	}
}
