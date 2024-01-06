package com.barraiser.onboarding.communication.channels.email;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class EmailServiceTest {
    @Test
    public void sendEmailTest() {
        final AmazonSimpleEmailService awsSES =
                AmazonSimpleEmailServiceClientBuilder.standard().build();

        final SendEmailRequest request =
                new SendEmailRequest()
                        .withDestination(
                                new Destination().withToAddresses("prabhat.ranjan32@gmail.com")
                                //                                        .withCcAddresses(ccEmails)
                                //
                                // .withBccAddresses(bccEmails)
                                )
                        .withMessage(
                                new Message()
                                        .withBody(
                                                new Body()
                                                        .withHtml(
                                                                new Content()
                                                                        .withData("Email body")))
                                        .withSubject(
                                                new Content()
                                                        .withCharset("UTF-8")
                                                        .withData("Testing Prabhat")))
                        .withSource("from<interview@barraiser.com>");

        awsSES.sendEmail(request);
    }
}
