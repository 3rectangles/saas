/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.services;

import com.barraiser.ats_integrations.dal.*;
import com.barraiser.ats_integrations.dto.ATSPartnerRepMappingsDTO;
import com.barraiser.ats_integrations.dto.UpdatePartnerRepMappingsDTO;
import com.barraiser.commons.dto.ats.enums.ATSProvider;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Log4j2
@AllArgsConstructor
public class PartnerRepConfigService {
	private final ATSPartnerRepMappingRepository atsPartnerRepMappingRepository;

	public void updatePartnerRepMappings(final UpdatePartnerRepMappingsDTO input) {
		input.getPartnerRepMappings()
				.forEach(x -> this.updatePartnerRepMapping(x, input.getPartnerId(), input.getAtsProvider()));
	}

	public void updatePartnerRepMapping(final UpdatePartnerRepMappingsDTO.PartnerRepMapping partnerRepMapping,
			final String partnerId, final ATSProvider atsProvider) {

		final ATSPartnerRepMappingDAO atsPartnerRepMappingDAO = this.atsPartnerRepMappingRepository
				.findByBrPartnerRepIdAndPartnerId(partnerRepMapping.getBrPartnerRepId(), partnerId)
				.orElse(
						ATSPartnerRepMappingDAO.builder()
								.id(UUID.randomUUID().toString())
								.brPartnerRepId(partnerRepMapping.getBrPartnerRepId())
								.partnerId(partnerId)
								.atsProvider(atsProvider)
								.build());

		this.atsPartnerRepMappingRepository.save(
				atsPartnerRepMappingDAO.toBuilder()
						.atsPartnerRepId(partnerRepMapping.getAtsPartnerRepId())
						.build());
	}

	public List<ATSPartnerRepMappingsDTO.PartnerRepMapping> getPartnerRepMappings(final String partnerId) {

		return this.atsPartnerRepMappingRepository.findByPartnerId(partnerId)
				.stream().map(
						x -> ATSPartnerRepMappingsDTO.PartnerRepMapping.builder()
								.brPartnerRepId(x.getBrPartnerRepId())
								.atsPartnerRepId(x.getAtsPartnerRepId())
								.atsProvider(x.getAtsProvider())
								.partnerId(x.getPartnerId())
								.build())
				.collect(Collectors.toList());
	}
}
