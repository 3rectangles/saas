package com.barraiser.communication.events;

import com.barraiser.communication.dal.ChannelConfigurationDAO;
import com.barraiser.communication.dal.ChannelConfigurationRepository;
import com.barraiser.communication.pojo.Channel;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class GetSlackChannels {

    private final ChannelConfigurationRepository channelConfigurationRepository;

    public List<Channel> getChannels(final String partnerId){
        final List<ChannelConfigurationDAO> channelList=this.channelConfigurationRepository.findAllByTargetEntityIdAndTargetEntityTypeAndChannelTypeAndDisabledOnIsNull(partnerId, "partner","slack");
        if(channelList!=null){
            return channelList.stream().map((channel)->Channel.builder()
                .channel(channel.getRecipient())
                .channelId(channel.getRecipientId())
                .build()).collect(Collectors.toList());
        }
        else{
            return null;
        }


    }


}
