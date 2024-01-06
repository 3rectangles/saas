package com.barraiser.communication.configurations;

import com.barraiser.communication.dal.ChannelConfigurationDAO;
import com.barraiser.communication.dal.ChannelConfigurationRepository;
import com.barraiser.communication.dal.NotificationDAO;
import com.barraiser.communication.dal.NotificationRepository;
import graphql.execution.DataFetcherResult;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Log4j2
@Component
@AllArgsConstructor
public class ConfigurationsRemover {
    private ChannelConfigurationRepository channelConfigurationRepository;
    private NotificationRepository notificationRepository;

    public Boolean removeSlackConfigurations(final String partnerId, final String channel, final String channelId){
        try{
            final ChannelConfigurationDAO channelToRemove = this.channelConfigurationRepository.findAllByTargetEntityIdAndRecipientAndRecipientIdAndDisabledOnIsNull(partnerId,channel,channelId);

            final List<NotificationDAO> disabledChannelEvents = this.notificationRepository.findAllByConfigIdAndDisabledOnIsNull(channelToRemove.getId());
            if(disabledChannelEvents.size()>0){
                disabledChannelEvents.stream().forEach((event)->{
                    event.setDisabledOn(Instant.now());
                    this.notificationRepository.save(event);

                });
            }

            channelToRemove.setDisabledOn(Instant.now());
            this.channelConfigurationRepository.save(channelToRemove);

            return Boolean.TRUE;
        }
        catch (Exception ex){
            log.info(ex);
            return Boolean.FALSE;
        }

    }
}
