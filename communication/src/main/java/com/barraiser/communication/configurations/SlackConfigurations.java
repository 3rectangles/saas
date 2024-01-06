package com.barraiser.communication.configurations;

import com.barraiser.communication.dal.ChannelConfigurationDAO;
import com.barraiser.communication.dal.ChannelConfigurationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Log4j2
@Component
@AllArgsConstructor
public class SlackConfigurations {
    private ChannelConfigurationRepository channelConfigurationRepository;

    public Boolean storeSlackConfigurations(final String state,final String channel, final String channelId, final String token){
        final ChannelConfigurationDAO channelExist = this.channelConfigurationRepository.findAllByTargetEntityTypeAndTargetEntityIdAndChannelTypeAndRecipientAndSecretsAndDisabledOnIsNull("partner",state,"slack",channel,token);
        log.info(channelExist);
        if(channelExist==null){
            final ChannelConfigurationDAO slack = ChannelConfigurationDAO.builder()
                .id(UUID.randomUUID().toString())
                .targetEntityId(state)
                .targetEntityType("partner")
                .recipient(channel)
                .recipientId(channelId)
                .secrets(token)
                .channelType("slack")
                .disabledOn(null)
                .build();

            this.channelConfigurationRepository.save(slack);

        }

        return Boolean.TRUE;
    }
}
