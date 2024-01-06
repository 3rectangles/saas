package com.barraiser.communication.automation.channels.email.recipient;

import com.barraiser.common.entity.Entity;
import com.barraiser.common.entity.EntityType;
import com.barraiser.common.graphql.types.Evaluation;
import com.barraiser.common.graphql.types.UserDetails;
import com.barraiser.communication.automation.QueryDataFetcher;
import com.barraiser.communication.automation.recipient.RecipientFetcher;
import com.barraiser.communication.automation.constants.RecipientType;
import com.barraiser.communication.automation.channels.email.dto.EmailRecipient;
import com.barraiser.communication.automation.userSubscription.UserCommunicationSubscriptionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component
@RequiredArgsConstructor
public class EmailRecipientForCandidateFromEvaluationFetcher implements RecipientFetcher<EmailRecipient> {
    private final static String GET_EVALUATION_QUERY = "query FetchEvaluation($input: GetEvaluationInput!) {\n" +
        "    getEvaluations(input: $input) {\n" +
        "      candidate {\n" +
        "        id\n" +
        "\temail\n" +
        "      }\n" +
        "      pocs {\n" +
        "        id\n" +
        "\temail\n" +
        "      }\n" +
        "    }\n" +
        "  }";

    private final UserCommunicationSubscriptionService userCommunicationSubscriptionService;
    private final QueryDataFetcher queryDataFetcher;
    private final ObjectMapper objectMapper;

    @Override
    public RecipientType getRecipientType() {
        return RecipientType.CANDIDATE;
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.EVALUATION;
    }

    @Override
    public EmailRecipient getRecipient(final Entity entity, final String eventType) {
        final Object queryData = this.queryDataFetcher.fetchQueryData(GET_EVALUATION_QUERY, entity);
        final Evaluation evaluation =
            this.objectMapper.convertValue(this.queryDataFetcher.getObjectFromPath(queryData, List.of("getEvaluations", "0")), Evaluation.class);
        final String candidateId = evaluation.getCandidate().getId();
        final String candidateEmail = evaluation.getCandidate().getEmail();
        List<UserDetails> pocs = evaluation.getPocs();
        pocs = this.getSubscribedPocs(pocs, eventType);

        if(!this.userCommunicationSubscriptionService.isUserSubscribedForEvent(candidateId, eventType)) {
            return null;
        }

        return EmailRecipient.builder()
            .toEmails(List.of(candidateEmail))
            .ccEmails(pocs.stream().map(UserDetails::getEmail).collect(Collectors.toList()))
            .build();
    }

    private List<UserDetails> getSubscribedPocs(final List<UserDetails> pocs, final String eventType) {
        return pocs.stream().filter(
            p -> p.getId() == null ||
                this.userCommunicationSubscriptionService.isUserSubscribedForEvent(p.getId(), eventType)
        ).collect(Collectors.toList());
    }
}
