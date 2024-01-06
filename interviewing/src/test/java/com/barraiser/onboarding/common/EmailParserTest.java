package com.barraiser.onboarding.common;

import com.barraiser.common.utilities.EmailParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class EmailParserTest {

    @Test
    public void testEmails() {
        final EmailParser emailParser = new EmailParser();
        emailParser.validateEmail("prabhat.ranjan32@gmail.com");
        emailParser.validateEmail("prabhat.ranjan32@gmail.co.in");
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateEmailWithSpace() {
        final EmailParser emailParser = new EmailParser();
        emailParser.validateEmail("prabhat.ranjan32 @gmail.co.in");
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateEmailWithoutAtTheRate() {
        final EmailParser emailParser = new EmailParser();
        emailParser.validateEmail("prabhat.ranjan32gmail.co.in");
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateEmpty() {
        final EmailParser emailParser = new EmailParser();
        emailParser.validateEmail("");
    }
}
