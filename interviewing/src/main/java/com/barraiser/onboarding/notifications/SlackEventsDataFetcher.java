package com.barraiser.onboarding.notifications;

import com.barraiser.communication.events.GetSlackEvents;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.common.graphql.input.GetSelectedEventsInput;
import graphql.execution.DataFetcherResult;
import com.barraiser.communication.pojo.SlackEvents;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Log4j2
@Component
@AllArgsConstructor
public class SlackEventsDataFetcher implements NamedDataFetcher {
    private final GraphQLUtil graphQLUtil;
    private final GetSlackEvents getSlackEvents;

    @Override
    public String name() {
        return "getSelectedEvents";
    }

    @Override
    public String type() {
        return QUERY_TYPE;
    }

    @Override
    public Object get(DataFetchingEnvironment environment) throws Exception{
        final GetSelectedEventsInput input = this.graphQLUtil.getArgument(environment, "input", GetSelectedEventsInput.class);

        List<SlackEvents> events = this.getSlackEvents.getEvents(input.getChannel(),input.getTargetEntityId(),input.getTargetEntityType());
        return DataFetcherResult.newResult()
            .data(events == null ? Collections.emptyList() : events)
            .build();
    }
}
