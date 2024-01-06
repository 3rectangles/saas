package com.barraiser.communication.events;

import com.barraiser.communication.dal.*;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Log4j2
@Component
@AllArgsConstructor
public class StoreEventData {
    private ChannelConfigurationRepository channelConfigurationRepository;
    private TemplateRepository templateRepository;
    private NotificationRepository notificationRepository;

    public Boolean storeEvents(final String partnerId, final String channel,final List<String> Event){
        final ChannelConfigurationDAO channel_info = this.channelConfigurationRepository.
            findAllByTargetEntityTypeAndTargetEntityIdAndChannelTypeAndRecipientAndDisabledOnIsNull("partner",partnerId,"slack", channel);

        final List<NotificationDAO> channelExist=this.notificationRepository.findAllByConfigIdAndDisabledOnIsNull(channel_info.getId());

        if(channelExist.size()==0){
            Event.stream().forEach((event)->{
                final NotificationDAO template=this.notificationRepository.findAllByEventTypeAndPartnerIdIsNull(event);

                final NotificationDAO notification = NotificationDAO.builder()
                    .id(UUID.randomUUID().toString())
                    .configId(channel_info.getId())
                    .partnerId(partnerId)
                    .template(template.getTemplate())
                    .eventType(event)
                    .disabledOn(null)
                    .build();
                this.notificationRepository.save(notification);

            });
        }
        else{
            channelExist.stream().forEach((currChannel)->{
                currChannel.setDisabledOn(Instant.now());
                this.notificationRepository.save(currChannel);
            });
            Event.stream().forEach((event)->{
                final NotificationDAO template=this.notificationRepository.findAllByEventTypeAndPartnerIdIsNull(event);
                final NotificationDAO newEvent=this.notificationRepository.findAllByEventTypeAndPartnerIdAndConfigId(event,partnerId,channel_info.getId());
                if(newEvent==null){
                    final NotificationDAO notification = NotificationDAO.builder()
                        .id(UUID.randomUUID().toString())
                        .configId(channel_info.getId())
                        .partnerId(partnerId)
                        .template(template.getTemplate())
                        .eventType(event)
                        .disabledOn(null).build();
                    this.notificationRepository.save(notification);
                }
                else{
                    newEvent.setDisabledOn(null);
                    this.notificationRepository.save(newEvent);
                }
            });
        }



        return Boolean.TRUE;
    }

}
