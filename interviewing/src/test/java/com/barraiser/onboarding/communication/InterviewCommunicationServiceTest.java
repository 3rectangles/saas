package com.barraiser.onboarding.communication;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@RunWith(MockitoJUnitRunner.class)
public class InterviewCommunicationServiceTest {
    @Test
    public void testDateFormat() {
        final ZoneId zoneId = ZoneId.systemDefault();
        System.out.println(zoneId);
        final OffsetDateTime interviewDate = OffsetDateTime.ofInstant(Instant.now(), ZoneId.of("Asia/Calcutta"));
        final String interviewDay = String.format("%02d.%02d.%d", interviewDate.getDayOfMonth(), interviewDate.getMonthValue(), interviewDate.getYear());
        final String interviewTime = String.format("%02d:%02d", interviewDate.getHour(), interviewDate.getMinute());
        System.out.println(interviewDay);
        System.out.println(interviewTime);
    }
}