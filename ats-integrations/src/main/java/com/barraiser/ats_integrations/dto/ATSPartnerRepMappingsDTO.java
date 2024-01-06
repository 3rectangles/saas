/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.dto;

import com.barraiser.commons.dto.ats.enums.ATSProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ATSPartnerRepMappingsDTO {

	private List<ATSPartnerRepMappingsDTO.PartnerRepMapping> partnerRepMappings;

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder(toBuilder = true)
	public static class PartnerRepMapping {

		private String partnerId;

		private ATSProvider atsProvider;

		private String atsPartnerRepId;

		private String brPartnerRepId;

	}

}
