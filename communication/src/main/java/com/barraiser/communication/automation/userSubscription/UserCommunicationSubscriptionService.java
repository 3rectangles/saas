package com.barraiser.communication.automation.userSubscription;

import com.barraiser.communication.automation.dal.UserCommunicationSubscriptionDAO;
import com.barraiser.communication.automation.dal.UserCommunicationSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Optional;

@Log4j2
@Component
@RequiredArgsConstructor
public class UserCommunicationSubscriptionService {
    private final UserCommunicationSubscriptionRepository userCommunicationSubscriptionRepository;

    public Boolean isUserSubscribedForEvent(final String userId, final String eventType) {
        final Optional<UserCommunicationSubscriptionDAO> userCommunicationSubscriptionDAO
            = this.userCommunicationSubscriptionRepository.findByUserIdAndEventType(userId, eventType);
        if(userCommunicationSubscriptionDAO.isEmpty()) {
            return true;
        }
        final String subscriptionRule = userCommunicationSubscriptionDAO.get().getSubscriptionRule();
        // TODO: Run rule
        return Boolean.TRUE.toString().equals(subscriptionRule.toLowerCase(Locale.ROOT));
    }
}
