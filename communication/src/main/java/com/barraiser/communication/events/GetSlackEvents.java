package com.barraiser.communication.events;

import com.barraiser.communication.dal.ChannelConfigurationDAO;
import com.barraiser.communication.dal.ChannelConfigurationRepository;
import com.barraiser.communication.dal.NotificationDAO;
import com.barraiser.communication.dal.NotificationRepository;
import com.barraiser.communication.pojo.SlackEvents;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class GetSlackEvents {

    private final ChannelConfigurationRepository channelConfigurationRepository;
    private final NotificationRepository notificationRepository;

    public List<SlackEvents> getEvents(final String channel, final String entityId, final String entityType){
        final ChannelConfigurationDAO requiredConfig = this.channelConfigurationRepository.findAllByTargetEntityTypeAndTargetEntityIdAndChannelTypeAndRecipientAndDisabledOnIsNull(entityType,entityId,"slack",channel);
        final List<NotificationDAO> allEvents = this.notificationRepository.findAllByConfigIdAndDisabledOnIsNull(requiredConfig.getId());

        if(allEvents!=null){
            return allEvents.stream().map((event)->SlackEvents.builder()
                .eventName(event.getEventType())
                .build()).collect(Collectors.toList());
        }
        else{
            return null;
        }

    }
}
