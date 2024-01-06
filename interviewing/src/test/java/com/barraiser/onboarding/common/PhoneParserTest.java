package com.barraiser.onboarding.common;

import com.barraiser.common.utilities.PhoneParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PhoneParserTest {

    @Test
    public void testParsing() {
        final PhoneParser phoneParser = new PhoneParser();

        final String phone = phoneParser.getFormattedPhone("917905401223");
        System.out.println(phone);
    }
}
