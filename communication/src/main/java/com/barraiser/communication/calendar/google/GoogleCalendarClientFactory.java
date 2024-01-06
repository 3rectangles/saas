package com.barraiser.communication.calendar.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GoogleCalendarClientFactory {
    private final String SERVICE_ACCOUNT =
            "barraiser-calendar@barraiser-scheduling.iam.gserviceaccount.com";
    private final String SERVICE_ACCOUNT_PRIVATE_KEY_FILE_PATH =
            "barraiser-scheduling-prabhat-service-account-key.p12";

    private static final String APPLICATION_NAME = "Calendar Application";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance(); // TBD
    private static final Map<String, Calendar> calendars = new HashMap<>();

    public Calendar get(final String email) throws GeneralSecurityException, IOException {
        if (calendars.get(email) == null) {
            calendars.put(email, getGoogleCalendarClient(email));
        }
        return calendars.get(email);
    }

    public Calendar getGoogleCalendarClient(final String email)
            throws GeneralSecurityException, IOException {
        final HttpTransport TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        final Credential credential = this.getCredentials(TRANSPORT, email);

        return new Calendar.Builder(TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private Credential getCredentials(final HttpTransport httpTransportClient, final String email)
            throws GeneralSecurityException, IOException {

        final KeyStore keystore = KeyStore.getInstance("PKCS12");
        keystore.load(
                this.getClass()
                        .getClassLoader()
                        .getResourceAsStream(this.SERVICE_ACCOUNT_PRIVATE_KEY_FILE_PATH),
                "notasecret".toCharArray());
        final PrivateKey pk =
                (PrivateKey) keystore.getKey("privatekey", "notasecret".toCharArray());

        final Credential credential =
                new GoogleCredential.Builder()
                        .setTransport(httpTransportClient) // TBD
                        .setJsonFactory(JSON_FACTORY)
                        .setServiceAccountId(this.SERVICE_ACCOUNT)
                        .setServiceAccountScopes(
                                List.of(CalendarScopes.CALENDAR, CalendarScopes.CALENDAR_EVENTS))
                        .setServiceAccountPrivateKey(pk)
                        .build()
                        .createDelegated(email);

        return credential;
    }
}
