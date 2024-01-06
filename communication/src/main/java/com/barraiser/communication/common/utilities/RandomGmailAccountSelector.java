package com.barraiser.communication.common.utilities;

import com.barraiser.communication.common.CommunicationAppProperties;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Log4j2
@Component
@AllArgsConstructor
public class RandomGmailAccountSelector {

    private final CommunicationAppProperties communicationAppProperties;

    public String getAccount() {
        final Random rand = new Random();
        final List<String> headlessGmailAccounts =
                this.communicationAppProperties.getCalendarAccounts();
        return headlessGmailAccounts.get(rand.nextInt(headlessGmailAccounts.size()));
    }
}
