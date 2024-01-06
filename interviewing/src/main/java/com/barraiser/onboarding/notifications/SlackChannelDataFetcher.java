package com.barraiser.onboarding.notifications;

import com.barraiser.communication.pojo.Channel;
import com.barraiser.communication.events.GetSlackChannels;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.common.graphql.input.GetChannelsForSlackInput;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Log4j2
@Component
@AllArgsConstructor
public class SlackChannelDataFetcher implements NamedDataFetcher{
    private final GraphQLUtil graphQLUtil;
    private final GetSlackChannels getSlackChannels;

    @Override
    public String name() {
        return "getChannelsForSlack";
    }

    @Override
    public String type() {
        return QUERY_TYPE;
    }

    @Override
    public Object get(DataFetchingEnvironment environment) throws Exception {
        final GetChannelsForSlackInput input = this.graphQLUtil.getArgument(environment, "input", GetChannelsForSlackInput.class);
        final List<Channel> channels = this.getSlackChannels.getChannels(input.getPartnerId());

        return DataFetcherResult.newResult()
            .data(channels == null ? Collections.emptyList() : channels)
            .build();
    }
}
