/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.communication.channels.email;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jose4j.json.internal.json_simple.JSONObject;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Component
@AllArgsConstructor
public class InterviewingZapierEmailService {

	public void sendEmailWithAllOptions(final String fromEmail,
			final String subject,
			final String body,
			final List<String> toEmail,
			final List<String> ccEmails, final List<String> bccEmails,
			final String emailHeader) throws IOException {

		// Create a JSON object with the required fields
		final JSONObject payload = new JSONObject();
		payload.put("subject", subject);
		payload.put("body", body);
		payload.put("to", toEmail);
		payload.put("cc", ccEmails);
		payload.put("bcc", bccEmails);
		payload.put("fromName", fromEmail);

		// Send the email to sales@barraiser.com through the Zapier hook
		final String zapierHookURL = "https://hooks.zapier.com/hooks/catch/9115561/3hiovsb/";
		HttpURLConnection connection = (HttpURLConnection) new URL(zapierHookURL).openConnection();
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type", "application/json");
		OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
		writer.write(payload.toString());
		writer.flush();

		int responseCode = connection.getResponseCode();
		InputStream inputStream;
		if (responseCode >= 200 && responseCode < 400) {
			inputStream = connection.getInputStream();
		} else {
			inputStream = connection.getErrorStream();
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		StringBuilder response = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			response.append(line);
		}
		reader.close();

	}
}
