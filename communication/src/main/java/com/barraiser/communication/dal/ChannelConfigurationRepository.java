package com.barraiser.communication.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface ChannelConfigurationRepository extends JpaRepository<ChannelConfigurationDAO, String> {

    List<ChannelConfigurationDAO> findAllByTargetEntityIdAndTargetEntityTypeAndChannelTypeAndDisabledOnIsNull(String targetEntityId, String targetEntityType, String channelType);

    ChannelConfigurationDAO findAllByTargetEntityTypeAndTargetEntityIdAndChannelTypeAndRecipientAndSecretsAndDisabledOnIsNull(String targetEntityType,String targetEntityId, String channelType,String recipient,String secrets);

    ChannelConfigurationDAO findAllByTargetEntityIdAndRecipientAndRecipientIdAndDisabledOnIsNull(String targetEntityId, String recipient, String recipientId);

    ChannelConfigurationDAO findAllByTargetEntityTypeAndTargetEntityIdAndChannelTypeAndRecipientAndDisabledOnIsNull(String targetEntityType,String targetEntityId, String channelType,String recipient);
}
