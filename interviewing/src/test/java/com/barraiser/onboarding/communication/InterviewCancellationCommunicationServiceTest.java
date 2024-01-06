package com.barraiser.onboarding.communication;

import lombok.extern.log4j.Log4j2;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * Integration test
 */
@Log4j2
@SpringBootTest
@ActiveProfiles("staging")
@RunWith(SpringRunner.class)
public class InterviewCancellationCommunicationServiceTest {

    @Autowired
    private InterviewCancellationCommunicationService interviewCancellationCommunicationService;


    @Test
    public void testCommunicateInterviewCancellationToStakeholders() {
        //this.interviewCancellationCommunicationService.communicateInterviewCancellationToStakeholders("650153bd-146d-4889-ba8e-5a6b5ca60dbc");
    }

}
